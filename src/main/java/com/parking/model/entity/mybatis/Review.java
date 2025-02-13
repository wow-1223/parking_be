package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("reviews")
public class Review {
    @TableId
    private Long id;
    private Long orderId;
    private Long userId;
    private Long parkingSpotId;
    private Byte rating;
    private String content;
    private String images;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}