package com.parking.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotDetailDTO extends ParkingSpotDTO {

    private OwnerDTO owner;

    private List<IntervalDTO> parkingIntervals;
    private List<IntervalDTO> occupiedIntervals;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerDTO {
        private String id;
        private String name;
        private String phone;
        private Double rating;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntervalDTO {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}