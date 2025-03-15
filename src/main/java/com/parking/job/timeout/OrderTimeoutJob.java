package com.parking.job.timeout;

import com.google.common.collect.Lists;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.enums.user.UserStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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

    @Autowired
    private AesUtil aesUtil;

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
            List<Order> orders = Lists.newArrayListWithCapacity(timeoutList.size());
            List<String[]> ownerMessages = Lists.newArrayListWithCapacity(timeoutList.size());
            for (OccupiedOrderDTO dto : timeoutList) {
                OccupiedSpot occupiedSpot = dto.getOccupiedSpot();
                Order order = dto.getOrder();
                order.setStatus(OrderStatusEnum.TIMEOUT.getStatus());
                User user = userMap.get(dto.getOrder().getUserId());
                if (user != null) {
                    user.setStatus(UserStatusEnum.VIOLATED.getStatus());
                    String phone = aesUtil.decrypt(user.getPhone());
                    String message = timeoutHandler.buildTimeoutMessage(occupiedSpot);
                    ownerMessages.add(new String[]{phone, message});
                }
                orders.add(order);
            }

            // 4. 批量处理超时订单
            timeoutHandler.batchProcessTimeoutOrders(orders, users, ownerMessages);
        } catch (Exception e) {
            log.error("Check timeout orders failed", e);
        }
    }
}