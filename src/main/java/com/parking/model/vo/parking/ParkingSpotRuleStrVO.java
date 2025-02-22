package com.parking.model.vo.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotRuleStrVO {
    private String mode;
    private String startTime;
    private String endTime;
    private List<String> specificDates;
    private List<String> specificWeekDays;
    private List<DateRangeVO> specificMonthDateRanges;
}
