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
    // status: 0:pending payment | 1:reserved | 2:confirmed | 3:processing | 4:completed | 5: canceling | 6: canceled | 7:refunding | 8:refunded | 9:overdue | 10: overdue pending payment | 11:leave temporarily
    private Integer status;

}
