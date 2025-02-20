package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.locationtech.jts.geom.Point;

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
    @TableField("coordinate")
    private Point coordinate;
    private String description;
    private BigDecimal price;
    private String images;
//    private Integer mode;
    private String rules;
    private String facilities;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}