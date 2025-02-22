package com.parking.model.vo.parking;

import com.parking.enums.parking.SpotModeEnum;
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

    @JsonIgnore
    private transient SpotModeEnum mode;
    @JsonIgnore
    private transient LocalTime startTime;
    @JsonIgnore
    private transient LocalTime endTime;
    @JsonIgnore
    private transient List<LocalDate> specificDates;
    @JsonIgnore
    private transient List<DayOfWeek> specificWeekDays;
    @JsonIgnore
    private List<DateRangeVO> specificMonthDateRanges;

    private ParkingSpotRuleStrVO ruleStr;

    // 构造函数中初始化

    public ParkingSpotRuleVO(ParkingSpotRuleStrVO ruleStr) {
        this.ruleStr = ruleStr;
    }

    public ParkingSpotRuleVO(SpotModeEnum mode, LocalTime startTime, LocalTime endTime) {
        this.ruleStr = new ParkingSpotRuleStrVO();
        this.mode = mode;
        this.ruleStr.setMode(mode.getDescription());
        this.startTime = startTime;
        this.ruleStr.setStartTime(startTime.toString());
        this.endTime = endTime;
        this.ruleStr.setEndTime(endTime.toString());
        this.specificDates = new ArrayList<>();
        this.ruleStr.setSpecificDates(new ArrayList<>());
        this.specificWeekDays = new ArrayList<>();
        this.ruleStr.setSpecificWeekDays(new ArrayList<>());
        this.specificMonthDateRanges = new ArrayList<>();
        this.ruleStr.setSpecificMonthDateRanges(new ArrayList<>());
    }

    // Getter方法中进行转换
    public SpotModeEnum getMode() {
        if (mode == null && ruleStr.getMode() != null) {
            mode = SpotModeEnum.getByDescription(ruleStr.getMode());
        }
        return mode;
    }

    public LocalTime getStartTime() {
        if (startTime == null && ruleStr.getStartTime() != null) {
            startTime = LocalTime.parse(ruleStr.getStartTime());
        }
        return startTime;
    }

    public LocalTime getEndTime() {
        if (endTime == null && ruleStr.getEndTime() != null) {
            endTime = LocalTime.parse(ruleStr.getEndTime());
        }
        return endTime;
    }

    public List<LocalDate> getSpecificDates() {
        if (specificDates == null && ruleStr.getSpecificDates() != null) {
            specificDates = ruleStr.getSpecificDates().stream()
                    .map(LocalDate::parse)
                    .collect(Collectors.toList());
        }
        return specificDates;
    }

    public List<DayOfWeek> getSpecificWeekDays() {
        if (specificWeekDays == null && ruleStr.getSpecificWeekDays() != null) {
            specificWeekDays = ruleStr.getSpecificWeekDays().stream()
                    .map(d -> DayOfWeek.of(Integer.parseInt(d)))
                    .collect(Collectors.toList());
        }
        return specificWeekDays;
    }

    public List<DateRangeVO> getSpecificMonthDateRanges() {
        if (specificMonthDateRanges == null && ruleStr.getSpecificMonthDateRanges() != null) {
            specificMonthDateRanges = ruleStr.getSpecificMonthDateRanges();
        }
        return specificMonthDateRanges;
    }

    // 其他方法保持不变
    public void addSpecificDate(LocalDate date) {
        getSpecificDates().add(date);
        ruleStr.getSpecificWeekDays().add(date.toString());
    }

    public void addSpecificWeekDay(DayOfWeek day) {
        getSpecificWeekDays().add(day);
        ruleStr.getSpecificWeekDays().add(String.valueOf(day.getValue()));
    }

    public void addSpecificMonthDateRange(DateRangeVO range) {
        if (specificMonthDateRanges == null) {
            specificMonthDateRanges = new ArrayList<>();
        }
        specificMonthDateRanges.add(range);
    }
}
