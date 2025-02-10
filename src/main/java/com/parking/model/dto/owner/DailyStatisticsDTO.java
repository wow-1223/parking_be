package com.parking.model.dto.owner;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 每日统计数据DTO
 */
@Data
public class DailyStatisticsDTO {

    /**
     * 日期，格式：yyyy-MM-dd
     */
    private String date;

    /**
     * 订单数量
     */
    private Long orderCount;

    /**
     * 收入金额
     */
    private BigDecimal amount;

    /**
     * 使用率(%)
     */
    private Double usageRate;

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
}