package com.parking.model.entity.jpa;

import lombok.Data;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parking_spots_id", nullable = false)
    private Long parkingSpotsId;

    @Column(name = "parking_period_id", nullable = false)
    private Long parkingPeriodId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;


} 