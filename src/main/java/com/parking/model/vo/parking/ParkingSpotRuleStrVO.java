package com.parking.model.vo.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotRuleStrVO {
    /**
     * 车位出租模式
     */
    private String mode;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    private List<String> specificDates;
    private List<String> specificWeekDays;
    private List<DateRangeVO> specificMonthDateRanges;
}
