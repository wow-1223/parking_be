package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.service.BaseOrderService;
import com.parking.service.user.UserOrderService;
import com.parking.handler.encrypt.AesUtil;
import com.parking.util.DateUtil;
import com.parking.handler.jwt.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserOrderServiceImpl extends BaseOrderService implements UserOrderService {

    private final AesUtil aesUtil;

    public UserOrderServiceImpl(AesUtil aesUtil) {
        super();
        this.aesUtil = aesUtil;
    }

    @Override
    public PageResponse<OrderDTO> getOrders(Long userId, Integer status, Integer page, Integer size) {
        // 查询订单
        IPage<Order> p = orderRepository.findByUserAndStatus(userId, status, page, size);
        return convertOrderPage(p);
    }

    @Override
    public DetailResponse<OrderDetailDTO> getOrderDetail(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        return convertOrderDetail(order);
    }

    @Override
    @Transactional
    public OperationResponse createOrder(CreateOrderRequest request) {
        Long userId = TokenUtil.getUserId();

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
        Order order = new Order();

        try {
            occupiedSpot.setParkingSpotId(spot.getId());
            occupiedSpot.setParkingDay(DateUtil.convertToLocalDate(request.getStartTime()));
            occupiedSpot.setStartTime(st);
            occupiedSpot.setEndTime(ed);
            occupiedSpotRepository.insert(occupiedSpot);

            order.setOwnerId(spot.getOwnerId());
            order.setUserId(userId);
            order.setParkingSpotId(spot.getId());
            order.setOccupiedSpotId(occupiedSpot.getId());
            order.setCarNumber(aesUtil.encrypt(request.getCarNumber()));
            order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getStatus());
            order.setAmount(calculateAmount(spot.getPrice(), st, ed));
            orderRepository.insert(order);

        } catch (Exception e) {
            log.error("Create order failed, order id: {}", order.getId(), e);
            throw new BusinessException("Create order failed, order id: " + order.getId());
        }

        return OperationResponse.operationSuccess(order.getId(), "create success");
    }

    @Override
    @Transactional
    public OperationResponse cancelOrder(OperateOrderRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), TokenUtil.getUserId());
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

        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(
                order.getOccupiedSpotId(), Lists.newArrayList("id"));
        if (occupiedSpot == null) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }

        try {
            // 释放停车位
            BigDecimal refundAmount = calculateRefundAmount(order, occupiedSpot.getStartTime());
            order.setStatus(OrderStatusEnum.CANCELING.getStatus());
            order.setRefundAmount(refundAmount);
            occupiedSpot.setDeletedAt(DateUtil.getCurrentTimestamp());

            orderRepository.update(order);
            occupiedSpotRepository.update(occupiedSpot);
        } catch (Exception e) {
            log.error("Cancel order failed, order id: {}", order.getId(), e);
            throw new BusinessException("Cancel order failed, order id: " + order.getId());
        }

        return OperationResponse.operationSuccess(request.getOrderId(), "cancel success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationResponse completeOrder(OperateOrderRequest request) {
        // 1. 查询订单
        Order order = orderRepository.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("Order not found");
        }

        // 2. 验证用户权限
        if (!order.getUserId().equals(TokenUtil.getUserId())) {
            throw new BusinessException("You are not allowed to complete this order");
        }

        // 3. 验证订单状态
        Set<Integer> availableStatus = Sets.newHashSet(
                OrderStatusEnum.PROCESSING.getStatus(),
                OrderStatusEnum.LEAVE_TEMPORARILY.getStatus(),
                OrderStatusEnum.OVERDUE.getStatus());
        if (!availableStatus.contains(order.getStatus())) {
            throw new BusinessException("Current order status is not allowed to cancel");
        }

        // 4. 查询占用记录
        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(order.getOccupiedSpotId());
        if (occupiedSpot == null) {
            throw new BusinessException("OccupiedSpot not found");
        }

        try {
            // 5. 更新订单状态
            order.setStatus(OrderStatusEnum.COMPLETED.getStatus());
            orderRepository.update(order);

            // 6. 删除占用记录（软删除）
            occupiedSpot.setDeletedAt(System.currentTimeMillis());
            occupiedSpotRepository.update(occupiedSpot);

            // 7. 记录实际结束时间
            orderRepository.update(order);

            log.info("Order completed successfully, order id: {}", order.getId());
            return OperationResponse.operationSuccess(order.getId(), "Complete order success");

        } catch (Exception e) {
            log.error("Complete order failed, order id: {}", order.getId(), e);
            throw new BusinessException("Complete order failed, order id: " + order.getId());
        }
    }

    /**
     * 计算退款金额
     * @param order 订单
     * @return 退款金额
     */
    public BigDecimal calculateRefundAmount(Order order, LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 如果订单未支付，全额退款
        if (OrderStatusEnum.PENDING_PAYMENT.getStatus() == order.getStatus()) {
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