package com.parking.service.owner.impl;

import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.owner.OwnerOrderListItemDTO;
import com.parking.model.entity.Order;
import com.parking.model.entity.User;
import com.parking.repository.OrderRepository;
import com.parking.service.owner.OwnerOrderService;

import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerOrderServiceImpl implements OwnerOrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public PageResponse<OwnerOrderListItemDTO> getOrders(String status, Integer page, Integer pageSize) {
        User currentUser = securityUtil.getCurrentUser();
        
        Page<Order> orderPage;
        if ("all".equals(status)) {
            status = null;
        }
        orderPage = orderRepository.findOrders(
                currentUser.getId(), null, status, PageRequest.of(page - 1, pageSize));

        List<OwnerOrderListItemDTO> orders = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(orderPage.getTotalElements(), orders);
    }
    
    private OwnerOrderListItemDTO convertToDTO(Order order) {
        OwnerOrderListItemDTO dto = new OwnerOrderListItemDTO();
        dto.setId(order.getId().toString());
        dto.setStartTime(order.getStartTime().toString());
        dto.setEndTime(order.getEndTime().toString());
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus());
        dto.setCarNumber(order.getCarNumber());
        
        // 设置车位信息
        OwnerOrderListItemDTO.ParkingSpotInfo parkingSpotInfo = 
                new OwnerOrderListItemDTO.ParkingSpotInfo();
        parkingSpotInfo.setId(order.getParkingSpot().getId().toString());
        parkingSpotInfo.setLocation(order.getParkingSpot().getLocation());
        dto.setParkingSpot(parkingSpotInfo);
        
        // 设置用户信息
        OwnerOrderListItemDTO.UserInfo userInfo = new OwnerOrderListItemDTO.UserInfo();
        userInfo.setId(order.getUser().getId().toString());
        userInfo.setName(order.getUser().getNickName());
        userInfo.setPhone(order.getUser().getPhone());
        dto.setUser(userInfo);
        
        return dto;
    }
} 