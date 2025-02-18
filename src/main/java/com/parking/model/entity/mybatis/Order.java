package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("orders")
public class Order {
    @TableId
    private Long id;
    private Long userId;
    private Long parkingSpotsId;
    private Long ownerId;
    private Long parkingOccupiedId;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private String carNumber;
    private String transactionId;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}