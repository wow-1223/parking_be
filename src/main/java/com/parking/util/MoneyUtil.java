package com.parking.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额工具类
 */
public class MoneyUtil {

    /**
     * 格式化金额，保留2位小数
     */
    public static BigDecimal format(BigDecimal amount) {
        return amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * 计算百分比
     */
    public static double calculatePercentage(long part, long total) {
        if (total == 0) {
            return 0;
        }
        return Math.round((double) part / total * 1000) / 10.0;
    }

    /**
     * 计算小时费用
     */
    public static BigDecimal calculateHourlyFee(BigDecimal price, long hours) {
        return price.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
    }
}