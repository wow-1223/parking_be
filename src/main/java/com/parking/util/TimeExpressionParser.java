package com.parking.util;

import com.parking.enums.parking.SpotModeEnum;
import com.parking.model.vo.parking.DateRangeVO;
import com.parking.model.vo.parking.ParkingSpotRuleVO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeExpressionParser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年M月d日");
    private static final Pattern SPECIFIC_DATE_PATTERN = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日)(\\d+)-(\\d+)点");
    private static final Pattern DAILY_PATTERN = Pattern.compile("每天(\\d+)-(\\d+)点");
    private static final Pattern WEEKLY_PATTERN = Pattern.compile("每周(.*?)(\\d+)-(\\d+)点");
    private static final Pattern MONTHLY_PATTERN = Pattern.compile("每月(.*?)(\\d+)-(\\d+)点");

    public static ParkingSpotRuleVO parse(String expression) {
        // 解析特定日期
        Matcher specificDateMatcher = SPECIFIC_DATE_PATTERN.matcher(expression);
        if (specificDateMatcher.matches()) {
            LocalDate date = LocalDate.parse(specificDateMatcher.group(1), DATE_FORMAT);
            LocalTime startTime = LocalTime.of(Integer.parseInt(specificDateMatcher.group(2)), 0);
            LocalTime endTime = LocalTime.of(Integer.parseInt(specificDateMatcher.group(3)), 0);
            ParkingSpotRuleVO interval = new ParkingSpotRuleVO(SpotModeEnum.SPECIFIC_DATE, startTime, endTime);
            interval.addSpecificDate(date);
            return interval;
        }

        // 解析每天规则
        Matcher dailyMatcher = DAILY_PATTERN.matcher(expression);
        if (dailyMatcher.matches()) {
            LocalTime startTime = LocalTime.of(Integer.parseInt(dailyMatcher.group(1)), 0);
            LocalTime endTime = LocalTime.of(Integer.parseInt(dailyMatcher.group(2)), 0);
            return new ParkingSpotRuleVO(SpotModeEnum.DAILY, startTime, endTime);
        }

        // 解析每周规则
        Matcher weeklyMatcher = WEEKLY_PATTERN.matcher(expression);
        if (weeklyMatcher.matches()) {
            String weekDaysStr = weeklyMatcher.group(1);
            LocalTime startTime = LocalTime.of(Integer.parseInt(weeklyMatcher.group(2)), 0);
            LocalTime endTime = LocalTime.of(Integer.parseInt(weeklyMatcher.group(3)), 0);
            ParkingSpotRuleVO interval = new ParkingSpotRuleVO(SpotModeEnum.WEEKLY, startTime, endTime);
            Arrays.stream(weekDaysStr.split("、"))
                    .map(TimeExpressionParser::parseWeekDay)
                    .forEach(interval::addSpecificWeekDay);
            return interval;
        }

        // 解析每月规则
        Matcher monthlyMatcher = MONTHLY_PATTERN.matcher(expression);
        if (monthlyMatcher.matches()) {
            String dateRangesStr = monthlyMatcher.group(1);
            LocalTime startTime = LocalTime.of(Integer.parseInt(monthlyMatcher.group(2)), 0);
            LocalTime endTime = LocalTime.of(Integer.parseInt(monthlyMatcher.group(3)), 0);
            ParkingSpotRuleVO interval = new ParkingSpotRuleVO(SpotModeEnum.MONTHLY, startTime, endTime);
            Arrays.stream(dateRangesStr.split("、"))
                    .map(TimeExpressionParser::parseDateRange)
                    .forEach(interval::addSpecificMonthDateRange);
            return interval;
        }

        throw new IllegalArgumentException("Unsupported time expression: " + expression);
    }

    private static DayOfWeek parseWeekDay(String weekDayStr) {
        switch (weekDayStr) {
            case "一":
                return DayOfWeek.MONDAY;
            case "二":
                return DayOfWeek.TUESDAY;
            case "三":
                return DayOfWeek.WEDNESDAY;
            case "四":
                return DayOfWeek.THURSDAY;
            case "五":
                return DayOfWeek.FRIDAY;
            case "六":
                return DayOfWeek.SATURDAY;
            case "日":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid week day: " + weekDayStr);
        }
    }

    private static DateRangeVO parseDateRange(String dateRangeStr) {
        String[] parts = dateRangeStr.split("-");
        int startDay = Integer.parseInt(parts[0]);
        int endDay = Integer.parseInt(parts[1]);
        return new DateRangeVO(startDay, endDay);
    }

}
