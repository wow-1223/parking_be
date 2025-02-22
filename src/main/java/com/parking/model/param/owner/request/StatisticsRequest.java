package com.parking.model.param.owner.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsRequest {

    private Long ownerId;

    private Long parkingSpotId;

    private String startTime;

    private String endTime;
}
