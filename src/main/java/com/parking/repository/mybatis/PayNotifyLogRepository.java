package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.mapper.PayNotifyLogMapper;
import com.parking.model.entity.mybatis.PayNotifyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PayNotifyLogRepository {

    @Autowired
    private PayNotifyLogMapper payNotifyLogMapper;

    /**
     * 根据订单号和交易号查询支付通知日志
     *
     * @param orderId 订单号
     * @param tradeNo 交易号
     * @return 支付通知日志
     */
    public PayNotifyLog findByOrderIdAndTradeNo(String orderId, String tradeNo) {
        return payNotifyLogMapper.selectOne(new QueryWrapper<PayNotifyLog>()
                .eq("order_id", orderId)
                .eq("trade_no", tradeNo));
    }

    /**
     * 插入支付通知日志
     *
     * @param payNotifyLog payNotifyLog
     */
    public void save(PayNotifyLog payNotifyLog) {
        payNotifyLogMapper.insert(payNotifyLog);
    }
}
