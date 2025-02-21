package com.parking.service;

import com.alipay.api.domain.Detail;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public interface OrderService {

    PageResponse<OrderDTO> getOrders(Long id, Integer status, Integer page, Integer size);

    DetailResponse<OrderDetailDTO> getOrderDetail(Long id);

    /**
     * 将订单实体转换为DTO
     */
    default OrderDTO convertToDTO(Order order, ParkingSpot parkingSpot, OccupiedSpot occupiedSpot) {

        OrderDTO dto = new OrderDTO();

        // 设置订单基本信息
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setCarNumber(order.getCarNumber());
        dto.setAmount(order.getAmount());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setTransactionId(order.getTransactionId());
        dto.setStatus(order.getStatus());

        // 设置停车位信息
        dto.setParkingSpotId(parkingSpot.getId());
        dto.setOwnerId(parkingSpot.getOwnerId());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLongitude(parkingSpot.getLongitude().doubleValue());
        dto.setLatitude(parkingSpot.getLatitude().doubleValue());

        // 设置占用信息
        if (occupiedSpot != null) {
            dto.setOccupiedSpotId(occupiedSpot.getId());
            dto.setStartTime(occupiedSpot.getStartTime());
            dto.setEndTime(occupiedSpot.getEndTime());
        }

        return dto;
    }

    /**
     * 计算订单金额
     * @param price 每小时价格
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单金额
     */
    default BigDecimal calculateAmount(BigDecimal price, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 验证时间
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("endTime must be after startTime");
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
    default BigDecimal calculateRefundAmount(Order order, LocalDateTime startTime) {
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
