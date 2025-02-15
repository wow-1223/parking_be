package com.parking.model.dto.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest {

    private String appId;

    private String orderId;

    private Long amount;

    private String description;

    private String payType;
}
