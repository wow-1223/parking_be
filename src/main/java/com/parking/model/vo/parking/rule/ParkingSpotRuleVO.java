package com.parking.model.vo.parking.rule;

import com.parking.enums.parking.SpotModeEnum;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ParkingSpotRuleVO {
    private String spotModeStr;
    private String startTimeStr;
    private String endTimeStr;
    private List<String> specificDatesStr;
    private List<String> specificWeekDaysStr;
    private List<DateRangeVO> specificMonthDateRanges;

    @JsonIgnore
    private transient SpotModeEnum spotMode;
    @JsonIgnore
    private transient LocalTime startTime;
    @JsonIgnore
    private transient LocalTime endTime;
    @JsonIgnore
    private transient List<LocalDate> specificDates;
    @JsonIgnore
    private transient List<DayOfWeek> specificWeekDays;

    // 构造函数中初始化
    public ParkingSpotRuleVO(SpotModeEnum spotMode, LocalTime startTime, LocalTime endTime) {
        this.spotMode = spotMode;
        this.spotModeStr = spotMode.getDescription();
        this.startTime = startTime;
        this.startTimeStr = startTime.toString();
        this.endTime = endTime;
        this.endTimeStr = endTime.toString();
        this.specificDates = new ArrayList<>();
        this.specificDatesStr = new ArrayList<>();
        this.specificWeekDays = new ArrayList<>();
        this.specificWeekDaysStr = new ArrayList<>();
        this.specificMonthDateRanges = new ArrayList<>();
    }

    // Getter方法中进行转换
    public SpotModeEnum getSpotMode() {
        if (spotMode == null && spotModeStr != null) {
            spotMode = SpotModeEnum.getByDescription(spotModeStr);
        }
        return spotMode;
    }

    public LocalTime getStartTime() {
        if (startTime == null && startTimeStr != null) {
            startTime = LocalTime.parse(startTimeStr);
        }
        return startTime;
    }

    public LocalTime getEndTime() {
        if (endTime == null && endTimeStr != null) {
            endTime = LocalTime.parse(endTimeStr);
        }
        return endTime;
    }

    public List<LocalDate> getSpecificDates() {
        if (specificDates == null && specificDatesStr != null) {
            specificDates = specificDatesStr.stream()
                    .map(LocalDate::parse)
                    .collect(Collectors.toList());
        }
        return specificDates;
    }

    public List<DayOfWeek> getSpecificWeekDays() {
        if (specificWeekDays == null && specificWeekDaysStr != null) {
            specificWeekDays = specificWeekDaysStr.stream()
                    .map(d -> DayOfWeek.of(Integer.parseInt(d)))
                    .collect(Collectors.toList());
        }
        return specificWeekDays;
    }

    // 其他方法保持不变
    public void addSpecificDate(LocalDate date) {
        getSpecificDates().add(date);
        specificDatesStr.add(date.toString());
    }

    public void addSpecificWeekDay(DayOfWeek day) {
        getSpecificWeekDays().add(day);
        specificWeekDaysStr.add(String.valueOf(day.getValue()));
    }

    public void addSpecificMonthDateRange(DateRangeVO range) {
        if (specificMonthDateRanges == null) {
            specificMonthDateRanges = new ArrayList<>();
        }
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
