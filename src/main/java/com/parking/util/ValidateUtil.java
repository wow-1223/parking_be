package com.parking.util;

import com.parking.common.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 验证工具类
 */
public class ValidateUtil {

    /**
     * 验证手机号格式
     */
    public static void validatePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    /**
     * 验证车牌号格式
     */
    public static void validateCarNumber(String carNumber) {
        if (!StringUtils.hasText(carNumber)) {
            throw new BusinessException("车牌号不能为空");
        }
        if (!carNumber.matches("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]$")) {
            throw new BusinessException("车牌号格式不正确");
        }
    }

    /**
     * 验证金额
     */
    public static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException("金额不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
    }

    /**
     * 验证经纬度
     */
    public static void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new BusinessException("经纬度不能为空");
        }
        if (latitude < -90 || latitude > 90) {
            throw new BusinessException("纬度范围为-90~90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new BusinessException("经度范围为-180~180");
        }
    }
}