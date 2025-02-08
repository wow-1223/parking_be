package com.parking.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "parking_spots")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String location;
    private Double latitude;
    private Double longitude;
    private BigDecimal price;
    private String image;
    private String status;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @ElementCollection
    private List<String> images;
    
    @ElementCollection
    private List<String> rules;
    
    @ElementCollection
    private List<String> facilities;
} 