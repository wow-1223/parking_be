package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("parking_spots")
public class ParkingSpot {
    @TableId
    private Long id;
    private Long ownerId;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String location;
    private BigDecimal longitude;
    private BigDecimal latitude;
//    @TableField("coordinate")
//    private Point coordinate;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String description;
    private BigDecimal price;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String images;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String rules;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String facilities;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}