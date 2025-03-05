package com.parking.model.param.owner.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateParkingRequest extends OwnerParkingRequest {

    /**
     * 车位id
     */
    private Long id;

    /**
     * 车位状态
     */
    private Integer status;
} 