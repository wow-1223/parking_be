package com.parking.model.dto.owner.response;

import com.parking.model.dto.owner.DailyStatisticsDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 使用统计响应DTO
 */
@Data
public class UsageStatisticsResponse {

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 已完成订单数
     */
    private Long completedOrders;

    /**
     * 已取消订单数
     */
    private Long cancelledOrders;

    /**
     * 总收入
     */
    private BigDecimal totalAmount;

    /**
     * 平均使用率(%)
     */
    private Double averageUsageRate;

    /**
     * 完成率(%)
     */
    private Double completionRate;

    /**
     * 取消率(%)
     */
    private Double cancellationRate;

    /**
     * 平均使用时长(小时)
     */
    private Double averageUsageHours;

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

    /**
     * 停车位ID
     * 如果为null则表示所有停车位的统计
     */
    private String parkingSpotId;
} 