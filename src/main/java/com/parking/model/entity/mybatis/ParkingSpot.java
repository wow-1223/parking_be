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
@TableName("parking_spots")
public class ParkingSpot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    @TableField(jdbcType = JdbcType.VARCHAR)
    private String location;
    private BigDecimal longitude;
    private BigDecimal latitude;
//    @TableField("coordinate")
//    private Point coordinate;
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String description;
    private BigDecimal price;
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String images;
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String rules;
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String facilities;
    private Integer parkingType;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}