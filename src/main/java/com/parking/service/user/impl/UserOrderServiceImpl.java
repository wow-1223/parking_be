package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.user.UserOrderService;
import com.parking.util.tool.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Override
    public PageResponse<OrderDTO> getOrders(Long userId, Integer status, Integer page, Integer size) {
        // 查询订单
        IPage<Order> p = orderRepository.findByOwnerAndStatus(userId, status, page, size);
        if (CollectionUtils.isEmpty(p.getRecords())) {
            return new PageResponse<>(0L, Collections.emptyList());
        }

        List<Order> records = p.getRecords();
        List<Long> parkingSpotIds = records.stream().map(Order::getParkingSpotsId).toList();
        List<Long> occupiedSpotIds = records.stream().map(Order::getParkingOccupiedId).toList();
        // 查询停车位信息
        List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll(
                parkingSpotIds, Lists.newArrayList("id", "owner_id", "longitude", "latitude", "location"));
        if (CollectionUtils.isEmpty(parkingSpots)) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }

        // 查询占用信息
        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findAll(
                occupiedSpotIds, Lists.newArrayList("id", "parking_day", "start_time", "end_time"));
        if (CollectionUtils.isEmpty(occupiedSpots)) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }

        Map<Long, ParkingSpot> parkingSpotMap = parkingSpots.stream()
                .collect(Collectors.toMap(ParkingSpot::getId, parkingSpot -> parkingSpot));
        Map<Long, OccupiedSpot> occupiedSpotMap = occupiedSpots.stream()
                .collect(Collectors.toMap(OccupiedSpot::getId, occupiedSpot -> occupiedSpot));

        List<OrderDTO> orders = Lists.newArrayListWithCapacity(records.size());
        for (Order order : records) {
            ParkingSpot parkingSpot = parkingSpotMap.get(order.getParkingSpotsId());
            if (parkingSpot == null) {
                throw new ResourceNotFoundException("ParkingSpot not found");
            }
            OccupiedSpot occupiedSpot = occupiedSpotMap.get(order.getParkingOccupiedId());
            if (occupiedSpot == null) {
                throw new ResourceNotFoundException("OccupiedSpot not found");
            }

            OrderDTO dto = convertToDTO(order, parkingSpot, occupiedSpot);
            orders.add(dto);
        }


        // 构建分页响应
        return new PageResponse<>(p.getTotal(), orders);
    }

    @Override
    @Transactional
    public OperationResponse createOrder(CreateOrderRequest request) {
        // 1. 验证停车位是否可用
        ParkingSpot parkingSpot = parkingSpotRepository.findById(request.getParkingSpotId());
        if (parkingSpot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }
        if (SpotStatusEnum.AVAILABLE.getStatus() != parkingSpot.getStatus()) {
            throw new BusinessException("ParkingSpot is not available");
        }

        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findByTime(request.getParkingSpotId(),
                DateUtil.parseDate(request.getStartTime()), DateUtil.parseDate(request.getEndTime()));
        if (CollectionUtils.isNotEmpty(occupiedSpots)) {
            throw new BusinessException("ParkingSpot has been occupied");
        }

        LocalDateTime st = DateUtil.parseDate(request.getStartTime());
        LocalDateTime ed = DateUtil.parseDate(request.getEndTime());

        OccupiedSpot occupiedSpot = new OccupiedSpot();
        occupiedSpot.setParkingSpotsId(parkingSpot.getId());
        occupiedSpot.setParkingDay(DateUtil.convertToDate(request.getStartTime()));
        occupiedSpot.setStartTime(st);
        occupiedSpot.setEndTime(ed);

        int occupiedSpotId = occupiedSpotRepository.insert(occupiedSpot);

        // 2. 创建订单
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setParkingSpotsId(parkingSpot.getId());
        order.setParkingOccupiedId((long) occupiedSpotId);
        order.setCarNumber(request.getCarNumber());
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getStatus());
        order.setAmount(calculateAmount(parkingSpot.getPrice(), st, ed));

        int orderId = orderRepository.insert(order);

        return OperationResponse.operationSuccess((long) orderId, "create success");
    }

    @Override
    @Transactional
    public OperationResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (OrderStatusEnum.PENDING_PAYMENT.getStatus() != order.getStatus()
                && OrderStatusEnum.CONFIRMED.getStatus() != order.getStatus()) {
            throw new BusinessException("Current order status is not allowed to cancel");
        }

        ParkingSpot parkingSpot = parkingSpotRepository.findById(order.getParkingSpotsId());
        if (parkingSpot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }

        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(order.getParkingOccupiedId());
        if (occupiedSpot == null) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }

        // 释放停车位
        BigDecimal refundAmount = calculateRefundAmount(order, occupiedSpot.getStartTime());
        order.setStatus(OrderStatusEnum.CANCELING.getStatus());
        order.setRefundAmount(refundAmount);
        occupiedSpot.setDeletedAt(DateUtil.getCurrentTimestamp());

        orderRepository.update(order);
        occupiedSpotRepository.update(occupiedSpot);

        return OperationResponse.operationSuccess(orderId, "cancel success");
    }
}