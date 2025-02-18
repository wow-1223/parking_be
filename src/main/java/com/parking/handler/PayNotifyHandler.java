package com.parking.handler;

import com.parking.constant.PayConstant;
import com.parking.event.PayNotifyEvent;
import com.parking.model.entity.mybatis.PayNotifyLog;
import com.parking.repository.mybatis.PayNotifyLogRepository;
import com.parking.service.user.UserOrderService;
import com.parking.util.tool.JsonUtil;
import com.parking.util.RedisLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PayNotifyHandler {

    @Autowired
    private UserOrderService userOrderService;

    @Autowired
    private PayNotifyLogRepository payNotifyLogRepository;

    @Autowired
    private RedisLockUtil redisLock;

    private static final String LOCK_KEY_PREFIX = "pay:notify:lock:";
    private static final long LOCK_EXPIRE_TIME = 30;

    @Async
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handlePayNotify(PayNotifyEvent event) {
        if (event == null) {
            log.warn("pay notify event is null");
            return;
        }

        String lockKey = LOCK_KEY_PREFIX + event.getOrderId();

        try {
            // 1. 先查询通知记录，避免不必要的加锁
            // 检查是否已处理过该通知
            PayNotifyLog existingLog = payNotifyLogRepository
                    .findByOrderIdAndTradeNo(event.getOrderId(), event.getTradeNo());
            if (existingLog != null) {
                log.info("pay notification for order {} has been processed, skipped", event.getOrderId());
                return;
            }

            // 尝试获取分布式锁，等待5秒，持有锁30秒
            boolean locked = redisLock.tryLock(lockKey, 5, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("order {} is being handled, skip duplicate notifications", event.getOrderId());
                return;
            }

            // 3. 双重检查，避免锁等待过程中其他线程已处理
            existingLog = payNotifyLogRepository
                    .findByOrderIdAndTradeNo(event.getOrderId(), event.getTradeNo());
            if (existingLog != null) {
                log.info("pay notification for order {} has been processed, skipped", event.getOrderId());
                return;
            }
            // 创建通知记录
            PayNotifyLog notifyLog = new PayNotifyLog();
            notifyLog.setOrderId(event.getOrderId());
            notifyLog.setTradeNo(event.getTradeNo());
            notifyLog.setPayType(event.getPayType().name());
            notifyLog.setNotifyTime(LocalDateTime.parse(event.getNotifyTime()));
            notifyLog.setNotifyParams(JsonUtil.toJson(event));
            notifyLog.setCreateTime(LocalDateTime.now());

            try {
                switch (event.getStatus()) {
                    case PayConstant.PayStatus.SUCCESS:
                        // todo 更新订单状态为支付成功
//                        orderService.updateOrderPaySuccess(
//                                event.getOrderId(),
//                                event.getTradeNo(),
//                                event.getPayType(),
//                                event.getAmount(),
//                                event.getNotifyTime()
//                        );
                        break;

                    case PayConstant.PayStatus.REFUNDED:
                        // todo 更新订单状态为已退款
//                        orderService.updateOrderRefunded(
//                                event.getOrderId(),
//                                event.getTradeNo(),
//                                event.getAmount(),
//                                event.getNotifyTime()
//                        );
                        break;
                }

                notifyLog.setStatus("SUCCESS");

            } catch (Exception e) {
                notifyLog.setStatus("FAILED");
                notifyLog.setErrorMsg(e.getMessage());
                throw e;
            } finally {
                // 保存通知记录
                payNotifyLogRepository.save(notifyLog);
            }

        } catch (Exception e) {
            log.error("handle pay notify event failed, event status: {}, order id: {}, error message: {}",
                    event.getStatus(), event.getOrderId(), e.getMessage());
            throw e;
        } finally {
            // 释放分布式锁
            redisLock.unlock(lockKey);
        }
    }
}