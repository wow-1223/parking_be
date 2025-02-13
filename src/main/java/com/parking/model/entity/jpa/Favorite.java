package com.parking.model.entity.jpa;

import lombok.Data;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parking_spot_id", nullable = false)
    private Long parkingSpotId;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;
} 