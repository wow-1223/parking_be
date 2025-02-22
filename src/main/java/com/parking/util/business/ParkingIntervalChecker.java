package com.parking.util.business;

import com.parking.model.vo.parking.DateRangeVO;
import com.parking.model.vo.parking.ParkingSpotRuleVO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ParkingIntervalChecker {

    public static boolean isInInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO rule) {
        return switch (rule.getMode()) {
            case SPECIFIC_DATE -> isInSpecificDateInterval(givenStartTime, givenEndTime, rule);
            case DAILY -> isInDailyInterval(givenStartTime, givenEndTime, rule);
            case WEEKLY -> isInWeeklyInterval(givenStartTime, givenEndTime, rule);
            case MONTHLY -> isInMonthlyInterval(givenStartTime, givenEndTime, rule);
            default -> false;
        };
    }

    private static boolean isInSpecificDateInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO rule) {
        List<LocalDate> specificDates = rule.getSpecificDates();
        for (LocalDate specificDate : specificDates) {
            if (givenStartTime.toLocalDate().equals(specificDate) && givenEndTime.toLocalDate().equals(specificDate)) {
                return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), rule.getStartTime(), rule.getEndTime());
            }
        }
        return false;
    }

    private static boolean isInDailyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO rule) {
        return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), rule.getStartTime(), rule.getEndTime());
    }

    private static boolean isInWeeklyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO rule) {
        List<DayOfWeek> specificWeekDays = rule.getSpecificWeekDays();
        DayOfWeek givenDay = givenStartTime.getDayOfWeek();
        if (specificWeekDays.contains(givenDay) && givenStartTime.toLocalDate().equals(givenEndTime.toLocalDate())) {
            return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), rule.getStartTime(), rule.getEndTime());
        }
        return false;
    }

    private static boolean isInMonthlyInterval(LocalDateTime givenStartTime, LocalDateTime givenEndTime, ParkingSpotRuleVO rule) {
        List<DateRangeVO> specificMonthDateRanges = rule.getSpecificMonthDateRanges();
        int givenDay = givenStartTime.getDayOfMonth();
        if (givenStartTime.toLocalDate().equals(givenEndTime.toLocalDate())) {
            for (DateRangeVO range : specificMonthDateRanges) {
                if (givenDay >= range.getStartDay() && givenDay <= range.getEndDay()) {
                    return isTimeWithinRange(givenStartTime.toLocalTime(), givenEndTime.toLocalTime(), rule.getStartTime(), rule.getEndTime());
                }
            }
        }
        return false;
    }

    private static boolean isTimeWithinRange(LocalTime givenStart, LocalTime givenEnd, LocalTime intervalStart, LocalTime intervalEnd) {
        return !givenStart.isAfter(intervalEnd) && !givenEnd.isBefore(intervalStart);
    }
}
