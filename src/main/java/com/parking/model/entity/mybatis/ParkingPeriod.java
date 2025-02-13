package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("parking_periods")
public class ParkingPeriod {
    @TableId
    private Long id;
    private Long parkingSpotsId;
    private String carNumber;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}