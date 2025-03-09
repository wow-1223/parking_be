package com.parking.job.timeout;

import com.parking.enums.order.OrderStatusEnum;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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
    private UserRepository userRepository;

    @Autowired
    private TimeoutHandler timeoutHandler;

    // 检测超时时长
    private static final Integer TIMEOUT = 5;

    /**
     * 每5分钟执行一次，检查超时订单
     */
    @Scheduled(cron = "0 */5 * * * *")
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
                            timeoutHandler.buildTimeoutMessage(dto.getOccupiedSpot())
                    ));
                }
            }

            // 5. 批量处理超时订单
            if (!timeoutOrders.isEmpty()) {
                timeoutHandler.batchProcessTimeoutOrders(timeoutOrders);
            }
        } catch (Exception e) {
            log.error("Check timeout orders failed", e);
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