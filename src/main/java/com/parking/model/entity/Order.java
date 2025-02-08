package com.parking.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "parking_spot_id")
    private ParkingSpot parkingSpot;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal amount;
    private String status; // pending, confirmed, completed, cancelled
    private String carNumber;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 