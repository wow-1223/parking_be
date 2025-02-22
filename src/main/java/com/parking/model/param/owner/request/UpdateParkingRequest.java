package com.parking.model.param.owner.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateParkingRequest extends OwnerParkingRequest {

    private Long id;

    private Integer status;
} 