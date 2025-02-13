package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("payments")
public class Payment {
    @TableId
    private Long id;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}