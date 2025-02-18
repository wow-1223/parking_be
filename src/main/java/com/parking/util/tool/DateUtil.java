package com.parking.util.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 格式化日期
     */
    public static String convertToDate(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATETIME_FORMATTER);
        return localDateTime.format(DATE_FORMATTER);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    /**
     * 格式化时间
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : null;
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    public static LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DATETIME_FORMATTER);
    }

    /**
     * 获取今天开始时间
     */
    public static LocalDateTime getTodayStart() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 获取今天结束时间
     */
    public static LocalDateTime getTodayEnd() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }

    /**
     * 获取本周开始时间
     */
    public static LocalDateTime getWeekStart() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay();
    }

    /**
     * 获取本月开始时间
     */
    public static LocalDateTime getMonthStart() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay();
    }
}