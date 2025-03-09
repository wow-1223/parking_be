package com.parking.job.timeout;

import com.parking.enums.order.OrderStatusEnum;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TimeoutHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SmsService smsService;

    /**
     * 批量处理超时订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchProcessTimeoutOrders(List<OrderTimeoutJob.TimeoutOrderData> timeoutOrders) {
        try {
            // 1. 批量更新订单状态
            List<Order> orders = timeoutOrders.stream()
                    .map(data -> {
                        data.getOrder().setStatus(OrderStatusEnum.TIMEOUT.getStatus());
                        return data.getOrder();
                    })
                    .collect(Collectors.toList());
            orderRepository.batchUpdate(orders);

            // 2. 批量发送提醒消息
            for (OrderTimeoutJob.TimeoutOrderData data : timeoutOrders) {
                try {
                    log.info("Send timeout message to user: {}, message: {}",
                            data.getPhone(), data.getMessage());
                    smsService.sendOrderConfirmMessage(data.getPhone(), data.getMessage());
                } catch (Exception e) {
                    log.error("Send timeout message failed, order id: {}",
                            data.getOrder().getId(), e);
                }
            }
        } catch (Exception e) {
            log.error("Batch process timeout orders failed", e);
            throw e; // 触发事务回滚
        }
    }

    /**
     * 构建超时提醒消息
     */
    public String buildTimeoutMessage(OccupiedSpot occupied) {
        String endTime = occupied.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您使用的停车位已超出预定时间(%s)，请尽快驶离车位，避免影响他人使用。超时过长将面临高额罚款与禁止使用租车位服务的处罚。",
                endTime
        );
    }

}
