package com.parking.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderResponse {
    private String orderId;
    private BigDecimal totalAmount;
    private String status;
} 