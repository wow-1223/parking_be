package com.parking.model.dto.owner;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 出租方停车位列表项DTO
 */
@Data
public class OwnerParkingListItemDTO {
    /**
     * 停车位ID
     */
    private String id;

    /**
     * 位置描述
     */
    private String location;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 每小时价格
     */
    private BigDecimal price;

    /**
     * 状态
     */
    private String status;

    /**
     * 当前订单信息
     */
    private CurrentOrderInfo currentOrder;

    /**
     * 统计信息
     */
    private Statistics statistics;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 当前订单信息
     */
    @Data
    public static class CurrentOrderInfo {
        /**
         * 订单ID
         */
        private String id;

        /**
         * 用户信息
         */
        private UserInfo user;

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
    }

    /**
     * 用户信息
     */
    @Data
    public static class UserInfo {
        /**
         * 用户ID
         */
        private String id;

        /**
         * 用户昵称
         */
        private String name;

        /**
         * 联系电话
         */
        private String phone;
    }

    /**
     * 统计信息
     */
    @Data
    public static class Statistics {
        /**
         * 总订单数
         */
        private Integer totalOrders;

        /**
         * 本月收入
         */
        private BigDecimal monthlyIncome;

        /**
         * 使用率(%)
         */
        private Double usageRate;

        /**
         * 评分
         */
        private Double rating;
    }
}