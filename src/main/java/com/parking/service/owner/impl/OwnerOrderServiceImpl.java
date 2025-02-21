package com.parking.service.owner.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.dto.owner.DailyStatisticsDTO;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.response.EarningsStatisticsResponse;
import com.parking.model.param.owner.response.UsageStatisticsResponse;
import com.parking.service.BaseOrderService;
import com.parking.service.owner.OwnerOrderService;

import com.parking.util.tool.DateUtil;
import com.parking.util.tool.MoneyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.parking.util.tool.DateUtil.DATE_FORMATTER;

@Service
public class OwnerOrderServiceImpl extends BaseOrderService implements OwnerOrderService {

    @Override
    public PageResponse<OrderDTO> getOrders(Long ownerId, Integer status, Integer page, Integer size) {
        // 查询订单
        IPage<Order> p = orderRepository.findByOwnerAndStatus(ownerId, status, page, size);
        return convertOrderPage(p);
    }

    @Override
    public DetailResponse<OrderDetailDTO> getOrderDetail(Long id) {
        return null;
    }

    @Override
    public EarningsStatisticsResponse getEarningsStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> statistics = orderRepository.getEarningsStatistics(
                ownerId,
                startDate,
                endDate
        );

        return buildEarningsResponse(statistics);
    }

    @Override
    public UsageStatisticsResponse getUsageStatistics(Long ownerId, Long parkingSpotId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> statistics;
        if (parkingSpotId != null) {
            statistics = orderRepository.getParkingUsageStatistics(
                    ownerId,
                    parkingSpotId,
                    startDate,
                    endDate
            );
        } else {
            statistics = orderRepository.getOverallUsageStatistics(
                    ownerId,
                    startDate,
                    endDate
            );
        }

        return buildUsageResponse(statistics);
    }

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
    private LocalDateTime parseEndDate(String endDate) {
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