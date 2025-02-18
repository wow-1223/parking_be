package com.parking.model.vo.parking.rule;

import com.parking.enums.parking.SpotModeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotRuleVO {

    //    SPECIFIC_DATE(0, "specific date"),
    //    DAILY(1, "daily"),
    //    WEEKLY(2, "weekly"),
    //    MONTHLY(3, "monthly");
    private SpotModeEnum spotMode;

    private LocalTime startTime;
    private LocalTime endTime;

    // 特定日期列表
    private List<LocalDate> specificDates;
    // 每周的特定星期几
    private List<DayOfWeek> specificWeekDays;
    // 每月的特定日期范围
    private List<DateRangeVO> specificMonthDateRanges;

    public ParkingSpotRuleVO(SpotModeEnum spotMode, LocalTime startTime, LocalTime endTime) {
        this.spotMode = spotMode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.specificDates = new ArrayList<>();
        this.specificWeekDays = new ArrayList<>();
        this.specificMonthDateRanges = new ArrayList<>();
    }

    public void addSpecificDate(LocalDate date) {
        specificDates.add(date);
    }

    public void addSpecificWeekDay(DayOfWeek day) {
        specificWeekDays.add(day);
    }

    public void addSpecificMonthDateRange(DateRangeVO range) {
        specificMonthDateRanges.add(range);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateRangeVO {
        private int startDay;
        private int endDay;
    }

}
