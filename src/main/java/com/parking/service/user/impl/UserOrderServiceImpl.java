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
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.service.BaseOrderService;
import com.parking.service.user.UserOrderService;
import com.parking.util.tool.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserOrderServiceImpl extends BaseOrderService implements UserOrderService {

    @Override
    public PageResponse<OrderDTO> getOrders(Long userId, Integer status, Integer page, Integer size) {
        // 查询订单
        IPage<Order> p = orderRepository.findByUserAndStatus(userId, status, page, size);
        return convertOrderPage(p);
    }

    @Override
    @Transactional
    public OperationResponse createOrder(CreateOrderRequest request) {
        // 1. 验证停车位是否可用
        ParkingSpot spot = parkingSpotRepository.findById(request.getParkingSpotId(), Lists.newArrayList("id", "owner_id", "price", "status"));
        if (spot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }
        if (SpotStatusEnum.AVAILABLE.getStatus() != spot.getStatus()) {
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
        occupiedSpot.setParkingSpotId(spot.getId());
        occupiedSpot.setParkingDay(DateUtil.convertToLocalDate(request.getStartTime()));
        occupiedSpot.setStartTime(st);
        occupiedSpot.setEndTime(ed);

        occupiedSpotRepository.insert(occupiedSpot);

        // 2. 创建订单
        Order order = new Order();
        order.setOwnerId(spot.getOwnerId());
        order.setUserId(request.getUserId());
        order.setParkingSpotId(spot.getId());
        order.setParkingOccupiedId(occupiedSpot.getId());
        order.setCarNumber(request.getCarNumber());
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getStatus());
        order.setAmount(calculateAmount(spot.getPrice(), st, ed));

        orderRepository.insert(order);

        return OperationResponse.operationSuccess(order.getId(), "create success");
    }

    @Override
    @Transactional
    public OperationResponse cancelOrder(CancelOrderRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), request.getUserId());
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (OrderStatusEnum.PENDING_PAYMENT.getStatus() != order.getStatus()
                && OrderStatusEnum.CONFIRMED.getStatus() != order.getStatus()) {
            throw new BusinessException("Current order status is not allowed to cancel");
        }

        Boolean spotExist = parkingSpotRepository.exist(order.getParkingSpotId());
        if (!spotExist) {
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

        return OperationResponse.operationSuccess(request.getOrderId(), "cancel success");
    }
}