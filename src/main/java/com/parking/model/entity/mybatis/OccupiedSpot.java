package com.parking.model.entity.mybatis;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("parking_occupied")
public class OccupiedSpot {
    @TableId
    private Long id;
    private Long parkingSpotsId;
//    private String carNumber;
    private String parkingDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}