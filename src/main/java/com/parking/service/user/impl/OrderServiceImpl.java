package com.parking.service.impl;

import com.parking.model.dto.*;
import com.parking.model.entity.Order;
import com.parking.model.entity.ParkingSpot;
import com.parking.repository.OrderRepository;
import com.parking.repository.ParkingSpotRepository;
import com.parking.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. 验证停车位是否可用
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(request.getParkingId()))
                .orElseThrow(() -> new ResourceNotFoundException("停车位不存在"));
                
        if (!"available".equals(parkingSpot.getStatus())) {
            throw new BusinessException("该停车位当前不可用");
        }
        
        // 2. 创建订单
        Order order = new Order();
        order.setParkingSpot(parkingSpot);
        order.setUser(getCurrentUser());
        order.setStartTime(LocalDateTime.parse(request.getStartTime()));
        order.setEndTime(LocalDateTime.parse(request.getEndTime()));
        order.setCarNumber(request.getCarNumber());
        order.setStatus("pending");
        order.setAmount(calculateAmount(parkingSpot.getPrice(), 
                order.getStartTime(), order.getEndTime()));
        order.setCreateTime(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // 3. 更新停车位状态
        parkingSpot.setStatus("occupied");
        parkingSpotRepository.save(parkingSpot);
        
        // 4. 返回响应
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId().toString());
        response.setTotalAmount(order.getAmount());
        response.setStatus(order.getStatus());
        
        return response;
    }

    @Override
    public PageResponse<OrderListItemDTO> getOrders(String status, Integer page, Integer pageSize) {
        return orderRepository.findOrders(getCurrentUser().getId(), status, page, pageSize);
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
                
        if (!order.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedException("无权操作此订单");
        }
        
        if (!"pending".equals(order.getStatus()) && !"confirmed".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可取消");
        }
        
        order.setStatus("cancelled");
        orderRepository.save(order);
        
        // 释放停车位
        ParkingSpot parkingSpot = order.getParkingSpot();
        parkingSpot.setStatus("available");
        parkingSpotRepository.save(parkingSpot);
        
        CancelOrderResponse response = new CancelOrderResponse();
        response.setSuccess(true);
        response.setRefundAmount(calculateRefundAmount(order));
        
        return response;
    }
} 