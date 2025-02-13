package com.parking.model.entity.jpa;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parking_spot_id", nullable = false)
    private Long parkingSpotId;

    @Column(name = "rating", nullable = false)
    private Byte rating;

    @Column(name = "content")
    private String content;

    @Column(name = "images")
    private String images;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;
}