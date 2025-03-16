package com.parking.job.refund;

import com.parking.enums.PayTypeEnum;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.factory.PayServiceFactory;
import com.parking.model.entity.mybatis.Order;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.service.payment.PayService;
import com.parking.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderRefundHandler {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PayServiceFactory payServiceFactory;

    private static final String REFUND_REASON = "User cancel order";

    /**
     * 处理单个订单退款
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    @Transactional(rollbackFor = Exception.class)
    protected void handleRefund(Order order) {
        log.info("Start processing refund for order: {}", order.getId());

        try {
            order.setStatus(OrderStatusEnum.REFUNDING.getStatus());
            orderRepository.update(order);
            log.info("Successfully refunded order: {}", order.getId());

            // 2. 获取对应的支付服务
            PayService payService = payServiceFactory.getPayService(PayTypeEnum.fromValue(order.getPayType()));

            // 3. 发起退款请求
            payService.refund(
                    String.valueOf(order.getId()),
                    order.getRefundAmount().longValue(),
                    REFUND_REASON
            );
        } catch (Exception e) {
            log.error("Exception occurred while processing refund for order: {}", order.getId(), e);
            throw e; // 触发事务回滚
        }
    }

    /**
     * 重试失败后的处理方法
     */
    @Recover
    public void recover(Exception e, Order order) {
        log.error("Handle refund retry failed after 3 attempts, order: {}", JsonUtil.toJson(order), e);
        // 重置订单状态为取消中
//        order.setStatus(OrderStatusEnum.CANCELING.getStatus());
//        orderRepository.update(order);
//        saveNotify(notify, e.getMessage(), PayConstant.PayStatus.FAILED);
    }
}
