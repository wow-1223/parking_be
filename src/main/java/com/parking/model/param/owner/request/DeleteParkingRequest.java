package com.parking.model.param.owner.request;

import lombok.Data;

@Data
public class DeleteParkingRequest {
    // private Long userId;
    /**
     * 车位id
     */
    private Long parkingSpotId;
}
