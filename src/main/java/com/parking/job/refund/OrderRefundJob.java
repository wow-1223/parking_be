package com.parking.job.refund;

import com.parking.enums.order.OrderStatusEnum;
import com.parking.model.entity.mybatis.Order;
import com.parking.repository.mybatis.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class OrderRefundJob {

    @Autowired
    private OrderRefundHandler orderRefundHandler;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 每5分钟扫描一次待退款订单
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scanRefundOrders() {
        log.info("Start scanning refund orders");
        try {
            // 1. 查询待退款订单
            List<Order> refundOrders = orderRepository.findByStatus(OrderStatusEnum.CANCELING.getStatus());
            if (CollectionUtils.isEmpty(refundOrders)) {
                return;
            }

            log.info("Found {} orders to refund", refundOrders.size());

            // 2. 处理每个待退款订单
            for (Order order : refundOrders) {
                try {
                    order.setStatus(OrderStatusEnum.REFUNDING.getStatus());
                    orderRefundHandler.handleRefund(order);
                } catch (Exception e) {
                    log.error("Failed to handle refund for order: {}", order.getId(), e);
                    // 这里可以添加重试次数记录，超过一定次数后通知管理员
                }
            }
        } catch (Exception e) {
            log.error("Failed to scan refund orders", e);
        }
    }
}