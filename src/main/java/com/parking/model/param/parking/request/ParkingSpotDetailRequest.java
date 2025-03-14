package com.parking.model.param.parking.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ParkingSpotDetailRequest {
    @NotEmpty(message = "车位id不能为空")
    private Long id;
    private String startTime;
    private String endTime;
}
