package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.enums.user.UserRoleEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.service.BaseOrderService;
import com.parking.service.lock.LockService;
import com.parking.service.user.UserOrderService;
import com.parking.handler.encrypt.AesUtil;
import com.parking.util.DateUtil;
import com.parking.handler.jwt.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.parking.constant.LockConstant.LockError.LOCK_STATUS_ERROR_CODE;
import static com.parking.enums.order.OrderStatusEnum.*;

@Slf4j
@Service
public class UserOrderServiceImpl extends BaseOrderService implements UserOrderService {

    private static final Set<Integer> ALLOW_CANCEL_STATUS = Sets.newHashSet(
            PENDING_PAYMENT.getStatus(),
            RESERVED.getStatus(),
            USER_OCCUPIED.getStatus(),
            UNKNOWN_OCCUPIED.getStatus()
    );

    private static final Set<Integer> ALLOW_COMPLETE_STATUS = Sets.newHashSet(
            PROCESSING.getStatus(),
            LEAVE_TEMPORARILY.getStatus(),
            TIMEOUT.getStatus()
    );

    @Autowired
    private AesUtil aesUtil;
    @Autowired
    private LockService lockService;

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

        Boolean exist = occupiedSpotRepository.checkExist(request.getParkingSpotId(),
                DateUtil.parseDate(request.getStartTime()), DateUtil.parseDate(request.getEndTime()));
        if (exist) {
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

        if (!ALLOW_CANCEL_STATUS.contains(order.getStatus())) {
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
            order.setStatus(CANCELING.getStatus());
            BigDecimal refundAmount = order.getAmount();
            if (OrderStatusEnum.USER_OCCUPIED.getStatus() != order.getStatus() &&
                    OrderStatusEnum.UNKNOWN_OCCUPIED.getStatus() != order.getStatus()) {
                // 非系统原因造成取消，计算退款金额
                refundAmount = calculateRefundAmount(order, occupiedSpot.getStartTime());
            }
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
        User user = userRepository.findById(order.getUserId(), Lists.newArrayList("id", "role"));
        if (!user.getId().equals(TokenUtil.getUserId()) && UserRoleEnum.ADMIN.getRole() != user.getRole()) {
            throw new BusinessException("You are not allowed to complete this order");
        }

        // 3. 验证订单状态
        if (!ALLOW_COMPLETE_STATUS.contains(order.getStatus())) {
            throw new BusinessException("Current order status is not allowed to cancel");
        }

        // 4. 查询占用记录
        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(order.getOccupiedSpotId());
        if (occupiedSpot == null) {
            throw new BusinessException("OccupiedSpot not found");
        }

        // 5. 验证车位锁状态
        ParkingSpot spot = parkingSpotRepository.findById(order.getParkingSpotId(), Lists.newArrayList("id", "device_id"));
        if (spot == null) {
            throw new BusinessException("ParkingSpot not found");
        }
        String lockStatus = lockService.getLockStatus(spot.getDeviceId());
        if (!Objects.equals(LockStatusEnum.LOWERED.getStatus(), lockStatus)) {
            throw new BusinessException(LOCK_STATUS_ERROR_CODE, "Lock status is not allowed to cancel");
        }

        try {
            // 5. 更新订单状态
            if (TIMEOUT.getStatus() == order.getStatus()) {
                // 超时订单，计算超时待支付金额
                order.setStatus(TIMEOUT_PENDING_PAYMENT.getStatus());
                order.setTimeoutAmount(calculateTimeoutAmount(order, DateUtil.getCurrentDateTime()));
            } else {
                order.setStatus(OrderStatusEnum.COMPLETED.getStatus());
                orderRepository.update(order);
            }

            // 6. 删除占用记录（软删除）
            occupiedSpot.setActualEndTime(DateUtil.getCurrentDateTime());
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

    @Override
    public void handlePaySuccess(Order order, PayNotifyVO notify) {
        if (OrderStatusEnum.PENDING_PAYMENT.getStatus() == order.getStatus()) {
            order.setStatus(RESERVED.getStatus());
        } else if (TIMEOUT_PENDING_PAYMENT.getStatus() == order.getStatus()) {
            order.setStatus(COMPLETED.getStatus());
        } else {
            log.error("Current order {} status is invalid for pay success", order.getId());
            throw new BusinessException("Current order status is invalid for pay success");
        }
        order.setTransactionId(notify.getTradeNo());
        orderRepository.update(order);
    }

    @Override
    public void handlePayRefunded(Order order, PayNotifyVO notify) {
        if (REFUNDING.getStatus() != order.getStatus()) {
            log.error("Current order {} status is invalid for pay refund", order.getId());
            throw new BusinessException("Current order status is invalid for pay refund");
        }
        order.setStatus(CANCELED.getStatus());
        order.setTransactionId(notify.getTradeNo());
        orderRepository.update(order);
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
//        if (Duration.between(now, startTime).toHours() < 2) {
//            return BigDecimal.ZERO;
//        }

        // 3. 根据距离开始时间计算退款比例
        BigDecimal refundRate;
        long minutesBeforeStart = Duration.between(now, startTime).toMinutes();

        if (minutesBeforeStart >= 15) {
            // 提前15min以上取消，全额退款
            refundRate = BigDecimal.ONE;
//        } else if (minutesBeforeStart >= 10) {
//            // 提前10min以上取消，退款90%
//            refundRate = new BigDecimal("0.90");
//        } else if (minutesBeforeStart >= 6) {
//            // 提前6-12小时取消，退款70%
//            refundRate = new BigDecimal("0.70");
        } else {
            // 提前15以内取消，退款50%
            refundRate = new BigDecimal("0.50");
        }

        // 4. 计算退款金额
        return order.getAmount()
                .multiply(refundRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTimeoutAmount(Order order, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutesAfterEnd = Duration.between(endTime, now).toMinutes();
        return order.getAmount().multiply(new BigDecimal(3 * minutesAfterEnd / 60.0));
    }
}