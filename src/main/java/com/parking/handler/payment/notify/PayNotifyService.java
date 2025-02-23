package com.parking.handler.payment.notify;

import com.parking.constant.PayConstant;
import com.parking.enums.PayTypeEnum;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.exception.PaymentException;
import com.parking.factory.PayServiceFactory;
import com.parking.handler.lock.RedisLockUtil;
import com.parking.handler.payment.PayService;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.PayNotifyLog;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.PayNotifyLogRepository;
import com.parking.util.DateUtil;
import com.parking.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PayNotifyService {

    private static final String LOCK_KEY_PREFIX = "pay:notify:lock:";
    private static final long LOCK_EXPIRE_TIME = 30;

    @Autowired
    private PayServiceFactory payServiceFactory;

    @Autowired
    private PayNotifyLogRepository payNotifyLogRepository;

    @Autowired
    private RedisLockUtil redisLock;

    @Autowired
    private OrderRepository orderRepository;

    public void verifyNotifySign(PayTypeEnum payType, Map<String, String> params) {
        PayService payService = payServiceFactory.getPayService(payType);
        payService.verifyNotifySign(params);
    }

    public PayNotifyVO generatePayNotifyVO(PayTypeEnum payType, Map<String, String> params) {
        PayService payService = payServiceFactory.getPayService(payType);
        return payService.generatePayNotifyVO(params);
    }

    public PayNotifyVO generateRefundNotifyVO(PayTypeEnum payType, Map<String, String> params) {
        PayService payService = payServiceFactory.getPayService(payType);
        return payService.generateRefundNotifyVO(params);
    }

    /**
     * 异步处理支付回调，失败后重试
     * maxAttempts: 最大重试次数(包括第一次)
     * backoff: 重试等待策略
     * delay: 重试等待时间
     * multiplier: 重试等待时间倍数
     */
    @Async("asyncExecutor")
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleNotify(PayNotifyVO notify) {
        log.info("Processing notify, params: {}", JsonUtil.toJson(notify));
        if (notify == null) {
            log.warn("notify is null");
            return;
        }

        String lockKey = LOCK_KEY_PREFIX + notify.getOrderId();
        try {
            // 幂等性检查
            Boolean skip = idempotentCheck(notify, lockKey);
            if (skip) {
                return;
            }

            // 处理支付回调
            handleOrder(notify);

            // 保存成功回调记录
            saveNotify(notify, null, PayConstant.PayStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Process notify failed, params: {}", JsonUtil.toJson(notify), e);
            throw new PaymentException("Process notify failed, error: " + e.getMessage());
        } finally {
            // 释放分布式锁
            redisLock.unlock(lockKey);
        }
    }

    /**
     * 重试失败后的处理方法
     */
    @Recover
    public void recover(Exception e, PayNotifyVO notify) {
        log.error("Pay notify retry failed after 3 attempts, params: {}", JsonUtil.toJson(notify), e);
        // 保存回调失败记录到数据库，后续可以通过定时任务重试
        saveNotify(notify, e.getMessage(), PayConstant.PayStatus.FAILED);
    }

    /**
     * 幂等性检查
     */
    private Boolean idempotentCheck(PayNotifyVO notify, String lockKey) {
        try {
            // 1. 先查询通知记录，避免不必要的加锁
            // 检查是否已处理过该通知
            PayNotifyLog existingLog = payNotifyLogRepository.findByOrderIdAndTradeNo(notify.getOrderId(), notify.getTradeNo());
            if (existingLog != null) {
                log.info("pay notification for order {} has been processed, skipped", notify.getOrderId());
                return true;
            }

            // 2. 尝试获取分布式锁，等待5秒，持有锁30秒
            boolean locked = redisLock.tryLock(lockKey, 5, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("order {} is being handled, skip duplicate notifications", notify.getOrderId());
                return true;
            }

            // 3. 双重检查，避免锁等待过程中其他线程已处理
            existingLog = payNotifyLogRepository.findByOrderIdAndTradeNo(notify.getOrderId(), notify.getTradeNo());
            if (existingLog != null) {
                log.info("pay notification for order {} has been processed, skipped", notify.getOrderId());
                return true;
            }
        } catch (Exception e) {
            log.error("check pay notify idempotence failed, notify status: {}, order id: {}, error message: {}",
                    notify.getStatus(), notify.getOrderId(), e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * 保存回调记录
     */
    private void saveNotify(PayNotifyVO notify, String message, String status) {

        // 1. 保存回调记录到数据库，后续可以通过定时任务重试
        log.info("Save {} notify record: {}", status, JsonUtil.toJson(notify));
        try {
            PayNotifyLog notifyLog = new PayNotifyLog();
            notifyLog.setOrderId(notify.getOrderId());
            notifyLog.setTradeNo(notify.getTradeNo());
            notifyLog.setPayType(notify.getPayType().name());
            notifyLog.setNotifyTime(DateUtil.parseDate(notify.getNotifyTime()));
            notifyLog.setNotifyParams(JsonUtil.toJson(notify));
            notifyLog.setCreateTime(LocalDateTime.now());
            notifyLog.setErrorMsg(message);
            notifyLog.setStatus(status);

            // 保存通知记录
            payNotifyLogRepository.save(notifyLog);
        } catch (Exception e) {
            log.error("Save {} notify record failed, notify status: {}, order id: {}, error message: {}",
                    status, notify.getStatus(), notify.getOrderId(), e.getMessage());
        }
    }

    /**
     * 处理支付回调: 更新订单状态
     */
    private void handleOrder(PayNotifyVO notify) {
        long orderId = Long.parseLong(notify.getOrderId());
        Boolean exist = orderRepository.exist(orderId);
        if (!exist) {
            log.warn("order {} not exist", orderId);
        }
        Order order = new Order();
        order.setId(Long.parseLong(notify.getOrderId()));
        order.setTransactionId(notify.getTradeNo());
        switch (notify.getStatus()) {
            case PayConstant.PayStatus.SUCCESS:
                order.setStatus(OrderStatusEnum.RESERVED.getStatus());
                orderRepository.update(order);
                break;
            case PayConstant.PayStatus.REFUNDED:
                order.setStatus(OrderStatusEnum.REFUNDED.getStatus());
                orderRepository.update(order);
                break;
            default:
                log.warn("notify status {} not support", notify.getStatus());
        }
    }

    /**
     * 解析支付宝回调参数
     */
    public Map<String, String> parseAlipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        if (params.get("notify_time") == null) {
            params.put("notify_time", DateUtil.formatTime(DateUtil.getCurrentDateTime()));
        }
        return params;
    }

    /**
     * 解析微信支付回调参数
     */
    public Map<String, String> parseWechatPayNotify(String notifyData, String signature,
                                                    String nonce, String timestamp, String serialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("notifyData", notifyData);
        params.put("signature", signature);
        params.put("nonce", nonce);
        params.put("timestamp", timestamp);
        params.put("serialNumber", serialNumber);
        return params;
    }
}