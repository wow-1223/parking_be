package com.parking.model.entity.jpa;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;
}