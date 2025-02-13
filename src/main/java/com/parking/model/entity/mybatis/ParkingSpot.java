package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("parking_spots")
public class ParkingSpot {
    @TableId
    private Long id;
    private Long ownerId;
    private String location;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String description;
    private BigDecimal price;
    private String images;
    private String rules;
    private String facilities;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}