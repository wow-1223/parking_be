package com.parking.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单列表项DTO
 */
@Data
public class OrderListItemDTO {
    /**
     * 订单ID
     */
    private String id;

    /**
     * 停车位信息
     */
    private ParkingSpotInfo parkingSpot;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 停车位信息
     */
    @Data
    public static class ParkingSpotInfo {
        /**
         * 停车位ID
         */
        private String id;

        /**
         * 位置描述
         */
        private String location;

        /**
         * 车位所有者信息
         */
        private OwnerInfo owner;
    }

    /**
     * 车位所有者信息
     */
    @Data
    public static class OwnerInfo {
        /**
         * 所有者ID
         */
        private String id;

        /**
         * 所有者昵称
         */
        private String name;

        /**
         * 联系电话
         */
        private String phone;
    }
}