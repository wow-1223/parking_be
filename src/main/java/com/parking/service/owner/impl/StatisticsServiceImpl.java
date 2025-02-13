package com.parking.service.owner.impl;

import com.parking.model.dto.owner.DailyStatisticsDTO;
import com.parking.model.dto.owner.response.EarningsStatisticsResponse;
import com.parking.model.dto.owner.response.UsageStatisticsResponse;
import com.parking.model.entity.jpa.User;
import com.parking.repository.jpa.OrderRepository;
import com.parking.service.owner.StatisticsService;
import com.parking.util.DateUtil;
import com.parking.util.MoneyUtil;
import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.parking.util.DateUtil.DATE_FORMATTER;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public EarningsStatisticsResponse getEarningsStatistics(String timeRange,
            String startDate, String endDate) {
        User currentUser = securityUtil.getCurrentUser();

        LocalDateTime start = parseStartDate(timeRange, startDate);
        LocalDateTime end = parseEndDate(timeRange, endDate);

        List<Object[]> statistics = orderRepository.getEarningsStatistics(
                currentUser.getId(),
                start,
                end
        );

        return buildEarningsResponse(statistics);
    }

    @Override
    public UsageStatisticsResponse getUsageStatistics(String parkingId, String timeRange,
            String startDate, String endDate) {
        User currentUser = securityUtil.getCurrentUser();

        LocalDateTime start = parseStartDate(timeRange, startDate);
        LocalDateTime end = parseEndDate(timeRange, endDate);

        List<Object[]> statistics;
        if (parkingId != null) {
            statistics = orderRepository.getParkingUsageStatistics(
                    Long.parseLong(parkingId),
                    start,
                    end
            );
        } else {
            statistics = orderRepository.getOverallUsageStatistics(
                    currentUser.getId(),
                    start,
                    end
            );
        }

        return buildUsageResponse(statistics);
    }

    // ... 其他辅助方法实现

    /**
     * 解析开始日期
     */
    private LocalDateTime parseStartDate(String timeRange, String startDate) {
        if (StringUtils.hasText(startDate)) {
            return LocalDate.parse(startDate, DATE_FORMATTER).atStartOfDay();
        }

        // 根据时间范围返回默认开始时间
        return switch (timeRange.toLowerCase()) {
            case "day" -> DateUtil.getTodayStart();
            case "week" -> DateUtil.getWeekStart();
            case "month" -> DateUtil.getMonthStart();
            default -> LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0);
        };
    }

    /**
     * 解析结束日期
     */
    private LocalDateTime parseEndDate(String timeRange, String endDate) {
        if (StringUtils.hasText(endDate)) {
            return LocalDate.parse(endDate, DATE_FORMATTER).plusDays(1).atStartOfDay();
        }

        // 默认为明天凌晨
        return LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * 构建收益统计响应
     */
    private EarningsStatisticsResponse buildEarningsResponse(List<Object[]> statistics) {
        EarningsStatisticsResponse response = new EarningsStatisticsResponse();
        List<DailyStatisticsDTO> dailyStats = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        long totalOrders = 0;

        for (Object[] stat : statistics) {
            LocalDate date = ((java.sql.Date) stat[0]).toLocalDate();
            BigDecimal amount = (BigDecimal) stat[1];
            Long orderCount = (Long) stat[2];

            DailyStatisticsDTO daily = new DailyStatisticsDTO();
            daily.setDate(date.format(DATE_FORMATTER));
            daily.setAmount(MoneyUtil.format(amount));
            daily.setOrderCount(orderCount);
            dailyStats.add(daily);

            totalAmount = totalAmount.add(amount);
            totalOrders += orderCount;
        }

        // 计算平均值
        int days = dailyStats.size();
        BigDecimal averageAmount = days > 0 ?
                MoneyUtil.format(totalAmount.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP)) :
                BigDecimal.ZERO;
        double averageOrders = days > 0 ? (double) totalOrders / days : 0;

        response.setDailyStatistics(dailyStats);
        response.setTotalAmount(MoneyUtil.format(totalAmount));
        response.setTotalOrders(totalOrders);
        response.setAverageAmount(averageAmount);
        response.setAverageOrders(Math.round(averageOrders * 10) / 10.0);

        return response;
    }

    /**
     * 构建使用统计响应
     */
    private UsageStatisticsResponse buildUsageResponse(List<Object[]> statistics) {
        UsageStatisticsResponse response = new UsageStatisticsResponse();
        List<DailyStatisticsDTO> dailyStats = new ArrayList<>();

        double totalUsageRate = 0;
        int validDays = 0;

        for (Object[] stat : statistics) {
            LocalDate date = ((java.sql.Date) stat[0]).toLocalDate();
            Double usageRate = (Double) stat[1];

            if (usageRate != null) {
                DailyStatisticsDTO daily = new DailyStatisticsDTO();
                daily.setDate(date.format(DATE_FORMATTER));
                daily.setUsageRate(Math.round(usageRate * 10) / 10.0);
                dailyStats.add(daily);

                totalUsageRate += usageRate;
                validDays++;
            }
        }

        // 计算平均使用率
        double averageUsageRate = validDays > 0 ? totalUsageRate / validDays : 0;

        response.setDailyStatistics(dailyStats);
        response.setAverageUsageRate(Math.round(averageUsageRate * 10) / 10.0);

        return response;
    }
} 