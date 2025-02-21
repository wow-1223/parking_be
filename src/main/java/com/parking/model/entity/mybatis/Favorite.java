package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorites")
public class Favorite {
    @TableId
    private Long id;
    private Long userId;
    private Long parkingSpotId;
    private LocalDateTime createTime;
    private Long deletedAt;
}