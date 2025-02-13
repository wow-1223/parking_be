package com.parking.model.entity.jpa;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * 停车位实体类
 */
@Data
@Entity
@Table(name = "parking_spots")
@DynamicUpdate
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 6)
    private Double longitude;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 6)
    private Double latitude;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Double price;

    @Column(name = "images")
    private String images;

    @Column(name = "rules")
    private String rules;

    @Column(name = "facilities")
    private String facilities;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;
} 