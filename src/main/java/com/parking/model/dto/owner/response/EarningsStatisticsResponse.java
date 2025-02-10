package com.parking.model.dto.owner.response;

import com.parking.model.dto.owner.DailyStatisticsDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 收益统计响应DTO
 */
@Data
public class EarningsStatisticsResponse {

    /**
     * 总收入
     */
    private BigDecimal totalAmount;

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 日均收入
     */
    private BigDecimal averageAmount;

    /**
     * 日均订单数
     */
    private Double averageOrders;

    /**
     * 环比增长率(%)
     */
    private Double growthRate;

    /**
     * 每日统计数据
     */
    private List<DailyStatisticsDTO> dailyStatistics;

    /**
     * 统计开始日期
     */
    private String startDate;

    /**
     * 统计结束日期
     */
    private String endDate;

    /**
     * 统计时间范围
     * day/week/month/custom
     */
    private String timeRange;
} 