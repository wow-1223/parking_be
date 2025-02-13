package com.parking.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class UserOrderVo {
    private Long userId;
    private String openId;
    private String phone;
    private String nickName;
    private String avatarUrl;
    private Byte role;
    private Byte status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;

    private Long orderId;
    private Long parkingSpotsId;
    private Long parkingPeriodId;
    private BigDecimal amount;
    private Byte orderStatus;
    private String paymentId;
    private Timestamp orderCreateTime;
    private Timestamp orderUpdateTime;
    private Long orderDeletedAt;
}