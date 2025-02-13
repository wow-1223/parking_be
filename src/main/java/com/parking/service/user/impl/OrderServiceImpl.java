package com.parking.service.user.impl;

import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.exception.UnauthorizedException;
import com.parking.model.dto.*;
import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.user.request.CreateOrderRequest;
import com.parking.model.dto.user.response.CancelOrderResponse;
import com.parking.model.dto.OrderListItemDTO;
import com.parking.model.entity.Order;
import com.parking.model.entity.ParkingSpot;
import com.parking.repository.OrderRepository;
import com.parking.repository.ParkingSpotRepository;
import com.parking.service.user.OrderService;
import com.parking.util.DateUtil;
import com.parking.util.PageUtil;
import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. 验证停车位是否可用
        ParkingSpot parkingSpot = parkingSpotRepository.findById(request.getParkingSpotId())
                .orElseThrow(() -> new ResourceNotFoundException("停车位不存在"));

        if (!"available".equals(parkingSpot.getStatus())) {
            throw new BusinessException("该停车位当前不可用");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setParkingSpot(parkingSpot);
        order.setUser(securityUtil.getCurrentUser());
        order.setStartTime(DateUtil.parseDate(request.getStartTime()));
        order.setEndTime(DateUtil.parseDate(request.getEndTime()));
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
        // 创建分页请求
        Pageable pageable = PageUtil.createTimeDescPageable(page, pageSize);

        Long uid = securityUtil.getCurrentUser().getId();
        if (status.equals("all")) {
            status = null;
        }

        // 查询订单
        Page<Order> orderPage = orderRepository.findOrders(uid, null, status, pageable);

        // 转换为DTO
        List<OrderListItemDTO> dtoList = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建分页响应
        return new PageResponse<>(orderPage.getTotalElements(), dtoList);
    }

    /**
     * 将订单实体转换为DTO
     */
    private OrderListItemDTO convertToDTO(Order order) {
        OrderListItemDTO dto = new OrderListItemDTO();

        // 设置订单基本信息
        dto.setId(order.getId().toString());
        dto.setCarNumber(order.getCarNumber());
        dto.setStartTime(DateUtil.formatDateTime(order.getStartTime()));
        dto.setEndTime(DateUtil.formatDateTime(order.getEndTime()));
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus());

        // 设置停车位信息
        ParkingSpot parkingSpot = order.getParkingSpot();
        OrderListItemDTO.ParkingSpotInfo parkingSpotInfo = new OrderListItemDTO.ParkingSpotInfo();
        parkingSpotInfo.setId(parkingSpot.getId().toString());
        parkingSpotInfo.setLocation(parkingSpot.getLocation());

        // 设置车位所有者信息
        OrderListItemDTO.OwnerInfo ownerInfo = new OrderListItemDTO.OwnerInfo();
        ownerInfo.setId(parkingSpot.getOwner().getId().toString());
        ownerInfo.setName(parkingSpot.getOwner().getNickName());
        ownerInfo.setPhone(parkingSpot.getOwner().getPhone());

        parkingSpotInfo.setOwner(ownerInfo);
        dto.setParkingSpot(parkingSpotInfo);

        return dto;
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));

        if (!order.getUser().getId().equals(securityUtil.getCurrentUser().getId())) {
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

    /**
     * 计算订单金额
     * @param price 每小时价格
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单金额
     */
    private BigDecimal calculateAmount(BigDecimal price, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 验证时间
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("结束时间不能早于开始时间");
        }

        // 2. 计算时长（小时）
        long hours = Duration.between(startTime, endTime).toHours();
        if (hours < 1) {
            hours = 1; // 不足1小时按1小时计算
        }

        // 3. 计算金额
        return price.multiply(BigDecimal.valueOf(hours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算退款金额
     * @param order 订单
     * @return 退款金额
     */
    private BigDecimal calculateRefundAmount(Order order) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = order.getStartTime();

        // 1. 如果订单未支付，全额退款
        if ("pending".equals(order.getStatus())) {
            return order.getAmount();
        }

        // 2. 如果距离开始时间不足2小时，不予退款
        if (Duration.between(now, startTime).toHours() < 2) {
            return BigDecimal.ZERO;
        }

        // 3. 根据距离开始时间计算退款比例
        BigDecimal refundRate;
        long hoursBeforeStart = Duration.between(now, startTime).toHours();

        if (hoursBeforeStart >= 24) {
            // 提前24小时以上取消，全额退款
            refundRate = BigDecimal.ONE;
        } else if (hoursBeforeStart >= 12) {
            // 提前12-24小时取消，退款90%
            refundRate = new BigDecimal("0.90");
        } else if (hoursBeforeStart >= 6) {
            // 提前6-12小时取消，退款70%
            refundRate = new BigDecimal("0.70");
        } else {
            // 提前2-6小时取消，退款50%
            refundRate = new BigDecimal("0.50");
        }

        // 4. 计算退款金额
        return order.getAmount()
                .multiply(refundRate)
                .setScale(2, RoundingMode.HALF_UP);
    }
} 