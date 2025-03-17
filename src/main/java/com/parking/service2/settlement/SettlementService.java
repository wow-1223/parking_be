package com.parking.service2.settlement;

/**
 * 结算服务
 */
public interface SettlementService {

    /**
     * 订单结算
     */
    void settleOrder(Long orderId);

    /**
     * 收益结算
     */
    void settleRevenue(Long orderId);


}
