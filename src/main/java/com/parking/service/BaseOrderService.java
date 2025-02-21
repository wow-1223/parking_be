package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.dto.user.UserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseOrderService implements OrderService {

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ParkingSpotRepository parkingSpotRepository;

    @Autowired
    protected OccupiedSpotRepository occupiedSpotRepository;

    public PageResponse<OrderDTO> convertOrderPage(IPage<Order> page) {
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return new PageResponse<>(0L, Collections.emptyList());
        }
        List<Order> records = page.getRecords();
        List<Long> parkingSpotIds = records.stream().map(Order::getParkingSpotId).toList();
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

        Map<Long, ParkingSpot> parkingSpotMap = parkingSpots.stream()
                .collect(Collectors.toMap(ParkingSpot::getId, parkingSpot -> parkingSpot));
        Map<Long, OccupiedSpot> occupiedSpotMap = occupiedSpots.stream()
                .collect(Collectors.toMap(OccupiedSpot::getId, occupiedSpot -> occupiedSpot));

        List<OrderDTO> orders = Lists.newArrayListWithCapacity(records.size());
        for (Order order : records) {
            ParkingSpot parkingSpot = parkingSpotMap.get(order.getParkingSpotId());
            if (parkingSpot == null) {
                throw new ResourceNotFoundException("ParkingSpot not found");
            }
            OccupiedSpot occupiedSpot = occupiedSpotMap.get(order.getParkingOccupiedId());
            OrderDTO dto = convertToDTO(order, parkingSpot, occupiedSpot);
            orders.add(dto);
        }


        // 构建分页响应
        return PageResponse.pageSuccess(orders, page.getTotal());
    }

    public DetailResponse<OrderDetailDTO> convertOrderDetail(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        // 设置订单基本信息
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setParkingSpotId(order.getParkingSpotId());
        dto.setOwnerId(order.getOwnerId());
        dto.setCarNumber(order.getCarNumber());
        dto.setAmount(order.getAmount());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setTransactionId(order.getTransactionId());
        dto.setStatus(order.getStatus());

        // 设置租户信息
        User owr = userRepository.findById(order.getOwnerId(), Lists.newArrayList("id", "nick_name", "phone"));
        if (owr == null) {
            throw new ResourceNotFoundException("Owner not found");
        }
        UserDTO owner = new UserDTO();
        owner.setId(owr.getId());
        owner.setName(owr.getNickName());
        owner.setPhone(owr.getPhone());
        dto.setOwner(owner);


        // 设置停车位信息
        ParkingSpot parkingSpot = parkingSpotRepository.findById(order.getParkingSpotId(),
                Lists.newArrayList("id", "owner_id", "longitude", "latitude", "location"));
        if (parkingSpot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }
        dto.setParkingSpotId(parkingSpot.getId());
        dto.setOwnerId(parkingSpot.getOwnerId());
        dto.setLongitude(parkingSpot.getLongitude().doubleValue());
        dto.setLatitude(parkingSpot.getLatitude().doubleValue());
        dto.setLocation(parkingSpot.getLocation());

        // 设置占用信息
        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(order.getParkingOccupiedId(),
                Lists.newArrayList("id", "parking_day", "start_time", "end_time"));
        if (occupiedSpot == null) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }
        dto.setStartTime(occupiedSpot.getStartTime());
        dto.setEndTime(occupiedSpot.getEndTime());

        return DetailResponse.detailSuccess(dto, "get order detail success");
    }
}
