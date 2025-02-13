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
    private Long parkingPeriodId;
    private BigDecimal amount;
    private Byte status;
    private String paymentId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}