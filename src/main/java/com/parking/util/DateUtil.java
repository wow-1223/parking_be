package com.parking.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 格式化日期
     */
    public static String convertToDateStr(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATETIME_FORMATTER);
        return localDateTime.format(DATE_FORMATTER);
    }

    public static LocalDate convertToLocalDate(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATETIME_FORMATTER);
        return localDateTime.toLocalDate();
    }

    public static LocalDate convertToLocalDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
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

    /**
     * 获取上一个0min或30min
     */
    public static LocalDateTime getLastHourOrHalfHour() {
        // 获取当前时间的 Calendar 实例
        Calendar calendar = Calendar.getInstance();

        // 获取当前分钟数
        int currentMinute = calendar.get(Calendar.MINUTE);

        if (currentMinute < 30) {
            // 如果当前分钟数小于 30，将分钟数设置为 0
            calendar.set(Calendar.MINUTE, 0);
        } else {
            // 如果当前分钟数大于等于 30，将分钟数设置为 30
            calendar.set(Calendar.MINUTE, 30);
        }

        // 将秒数和毫秒数设置为 0
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 返回上一个整点或 30 分钟的 Date 对象
        return parseDate(calendar.getTime().toString());
    }

    /**
     * 获取下一个0min或30min
     */
    public static LocalDateTime getNextHourOrHalfHour() {
        // 获取当前时间的 Calendar 实例
        Calendar calendar = Calendar.getInstance();

        // 获取当前分钟数
        int currentMinute = calendar.get(Calendar.MINUTE);

        if (currentMinute < 30) {
            // 如果当前分钟数小于 30，将分钟数设置为 30
            calendar.set(Calendar.MINUTE, 30);
        } else {
            // 如果当前分钟数大于等于 30，将小时数加 1，分钟数设置为 0
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 0);
        }

        // 将秒数和毫秒数设置为 0
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 返回下一个整点或 30 分钟的 Date 对象
        return parseDate(calendar.getTime().toString());
    }
}