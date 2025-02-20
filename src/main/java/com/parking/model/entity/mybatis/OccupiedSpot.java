package com.parking.model.entity.mybatis;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("parking_occupied")
public class OccupiedSpot {
    @TableId
    private Long id;
    private Long parkingSpotsId;
    private LocalDate parkingDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}