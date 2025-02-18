package com.parking.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private Long userId;
    private Long parkingSpotId;
    private Long ownerId;
    private String location;
    private Double longitude;
    private Double latitude;
    private Long occupiedSpotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String carNumber;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private String transactionId;
    private Integer status;

}
