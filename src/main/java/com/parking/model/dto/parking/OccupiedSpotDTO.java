package com.parking.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupiedSpotDTO {
    private Long id;
    private Long parkingSpotId;
    private LocalDate parkingDay;
    private String startTime;
    private String endTime;
}
