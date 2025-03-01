package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long parkingSpotId;
    private Long ownerId;
    @TableField(value = "parking_occupied_id")
    private Long occupiedSpotId;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private String payType;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String carNumber;
    private String transactionId;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}