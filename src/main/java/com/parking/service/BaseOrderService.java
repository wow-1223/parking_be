package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.PageResponse;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
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
        if (CollectionUtils.isEmpty(occupiedSpots)) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }

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
            if (occupiedSpot == null) {
                throw new ResourceNotFoundException("OccupiedSpot not found");
            }

            OrderDTO dto = convertToDTO(order, parkingSpot, occupiedSpot);
            orders.add(dto);
        }


        // 构建分页响应
        return new PageResponse<>(page.getTotal(), orders);
    }
}
