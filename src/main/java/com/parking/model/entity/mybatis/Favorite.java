package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("favorites")
public class Favorite {
    @TableId
    private Long id;
    private Long userId;
    private Long parkingSpotId;
    private Timestamp createTime;
    private Long deletedAt;
}