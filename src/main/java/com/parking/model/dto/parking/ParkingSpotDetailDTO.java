package com.parking.model.dto.parking;

import com.parking.model.dto.user.UserDTO;
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

    private String description;
    private List<String> images;
    private List<String> facilities;
//    private List<ParkingSpotRuleVO> rules;

    private UserDTO owner;
    private List<IntervalDTO> parkingIntervals;
    private List<IntervalDTO> occupiedIntervals;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntervalDTO {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}