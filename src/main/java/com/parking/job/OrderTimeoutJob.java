package com.parking.job;

import com.parking.enums.order.OrderStatusEnum;
import com.parking.handler.sms.SmsService;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderTimeoutJob {

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 检测超时时长
    private static final Integer TIMEOUT = 5;

    /**
     * 每分钟执行一次，检查超时订单
     */
    @Scheduled(cron = "5 * * * * ?")
    public void checkTimeoutOrders() {
        log.info("Start checking timeout orders");
        try {
            // 1. 联表查询即将超时的订单和占用信息
            LocalDateTime checkTime = LocalDateTime.now();
            List<OccupiedOrderDTO> timeoutList = occupiedSpotRepository.findTimeoutSpotsWithOrders(
                    checkTime,
                    TIMEOUT,
                    OrderStatusEnum.PROCESSING.getStatus()
            );

            if (CollectionUtils.isEmpty(timeoutList)) {
                return;
            }

            // 2. 批量查询用户信息
            List<Long> userIds = timeoutList.stream()
                    .map(dto -> dto.getOrder().getUserId())
                    .distinct()
                    .collect(Collectors.toList());

            List<User> users = userRepository.findByIds(userIds);
            if (CollectionUtils.isEmpty(users)) {
                return;
            }
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));

            // 3. 准备批量处理的数据
            List<TimeoutOrderData> timeoutOrders = new ArrayList<>();
            for (OccupiedOrderDTO dto : timeoutList) {
                User user = userMap.get(dto.getOrder().getUserId());
                if (user != null) {
                    timeoutOrders.add(new TimeoutOrderData(
                            dto.getOrder(),
                            user.getPhone(),
                            buildTimeoutMessage(dto.getOccupiedSpot())
                    ));
                }
            }

            // 5. 批量处理超时订单
            if (!timeoutOrders.isEmpty()) {
                batchProcessTimeoutOrders(timeoutOrders);
            }
        } catch (Exception e) {
            log.error("Check timeout orders failed", e);
        }
    }

    /**
     * 构建超时提醒消息
     */
    private String buildTimeoutMessage(OccupiedSpot occupied) {
        String endTime = occupied.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您使用的停车位已超出预定时间(%s)，请尽快驶离车位，避免影响他人使用。超时过长将面临高额罚款与禁止使用租车位服务的处罚。",
                endTime
        );
    }

    /**
     * 批量处理超时订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchProcessTimeoutOrders(List<TimeoutOrderData> timeoutOrders) {
        try {
            // 1. 批量更新订单状态
            List<Order> orders = timeoutOrders.stream()
                    .map(data -> {
                        data.getOrder().setStatus(OrderStatusEnum.OVERDUE.getStatus());
                        return data.getOrder();
                    })
                    .collect(Collectors.toList());
            orderRepository.batchUpdate(orders);

            // 2. 批量发送提醒消息
            for (TimeoutOrderData data : timeoutOrders) {
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

    @Data
    @AllArgsConstructor
    public static class TimeoutOrderData {
        private Order order;
        private String phone;
        private String message;
    }
}