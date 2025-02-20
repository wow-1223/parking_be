package com.parking.handler;

import com.parking.enums.parking.SpotModeEnum;
import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ParkingIntervalChecker {

    public static boolean isInInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO interval) {
        return switch (interval.getSpotMode()) {
            case SPECIFIC_DATE -> isInSpecificDateInterval(givenStartTime, givenEndTime, interval);
            case DAILY -> isInDailyInterval(givenStartTime, givenEndTime, interval);
            case WEEKLY -> isInWeeklyInterval(givenStartTime, givenEndTime, interval);
            case MONTHLY -> isInMonthlyInterval(givenStartTime, givenEndTime, interval);
            default -> false;
        };
    }

    private static boolean isInSpecificDateInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO interval) {
        List<LocalDate> specificDates = interval.getSpecificDates();
        for (LocalDate specificDate : specificDates) {
            if (givenStartTime.toLocalDate().equals(specificDate) && givenEndTime.toLocalDate().equals(specificDate)) {
                return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), interval.getStartTime(), interval.getEndTime());
            }
        }
        return false;
    }

    private static boolean isInDailyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO interval) {
        return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), interval.getStartTime(), interval.getEndTime());
    }

    private static boolean isInWeeklyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO interval) {
        List<DayOfWeek> specificWeekDays = interval.getSpecificWeekDays();
        DayOfWeek givenDay = givenStartTime.getDayOfWeek();
        if (specificWeekDays.contains(givenDay) && givenStartTime.toLocalDate().equals(givenEndTime.toLocalDate())) {
            return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), interval.getStartTime(), interval.getEndTime());
        }
        return false;
    }

    private static boolean isInMonthlyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO interval) {
        List<ParkingSpotRuleVO.DateRangeVO> specificMonthDateRanges = interval.getSpecificMonthDateRanges();
        int givenDay = givenStartTime.getDayOfMonth();
        if (givenStartTime.toLocalDate().equals(givenEndTime.toLocalDate())) {
            for (ParkingSpotRuleVO.DateRangeVO range : specificMonthDateRanges) {
                if (givenDay >= range.getStartDay() && givenDay <= range.getEndDay()) {
                    return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), interval.getStartTime(), interval.getEndTime());
                }
            }
        }
        return false;
    }

    private static boolean isTimeWithinRange(LocalTime givenStart, LocalTime givenEnd, LocalTime intervalStart, LocalTime intervalEnd) {
        return !givenStart.isAfter(intervalEnd) && !givenEnd.isBefore(intervalStart);
    }
}
