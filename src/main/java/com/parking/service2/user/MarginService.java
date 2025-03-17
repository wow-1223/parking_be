package com.parking.service2.user;

import com.parking.model.param.common.OperationResponse;

import java.math.BigDecimal;

/**
 * 保证金服务
 */
public interface MarginService {

    /**
     * 支付保证金
     */
    OperationResponse payMargin(Long userId, BigDecimal margin);

    /**
     * 退还保证金
     */
    OperationResponse returnMargin(Long userId, BigDecimal margin);

}
