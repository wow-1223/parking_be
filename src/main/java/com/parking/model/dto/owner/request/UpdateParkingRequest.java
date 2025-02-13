package com.parking.model.dto.owner.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateParkingRequest extends OwnerParkingRequest {
    private Long id;
    private String status;
} 