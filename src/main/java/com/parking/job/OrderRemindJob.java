package com.parking.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.handler.sms.SmsService;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderRemindJob {

//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private SmsService smsService;
//
//    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
//    private static final int REMIND_MINUTES = 5;
//
//    /**
//     * 每分钟执行一次，检查需要提醒的订单
//     */
//    @Scheduled(cron = "0 * * * * ?")
//    public void checkOrdersToRemind() {
//        log.info("Start checking orders to remind");
//        try {
//            // 1. 查询即将到期的订单（5分钟内到期的进行中订单）
//            LocalDateTime checkTime = LocalDateTime.now().plusMinutes(REMIND_MINUTES);
//            List<OccupiedOrderDTO> expiringOrders = orderRepository.findExpiringOrders(
//                    checkTime,
//                    OrderStatusEnum.PROCESSING.getStatus()
//            );
//
//            if (CollectionUtils.isEmpty(expiringOrders)) {
//                return;
//            }
//
//            log.info("Found {} orders to remind", expiringOrders.size());
//
//            // 2. 获取所有相关用户信息
//            // 当前订单用户
//            List<Integer> userIds = expiringOrders.stream()
//                    .map(dto -> dto.getOrder().getUserId()).collect(Collectors.toList());
//
//            // 查询这些车位是否有后续预订
//            List<OccupiedOrderDTO> nextOrders = orderRepository.findNextOrders(
//                    expiringOrders.stream()
//                            .map(dto -> dto.getOccupiedSpot().getParkingSpotId())
//                            .collect(Collectors.toList()),
//                    checkTime
//            );
//
//            // 后续预订的用户
//            if (CollectionUtils.isNotEmpty(nextOrders)) {
//                userIds.addAll(nextOrders.stream()
//                        .map(OccupiedOrderDTO::getOrder)
//                        .collect(Collectors.toList()));
//            }
//
//            // 3. 批量查询用户信息
//            List<User> users = userRepository.findByIds(userIds.stream().distinct().collect(Collectors.toList()));
//            if (CollectionUtils.isEmpty(users)) {
//                return;
//            }
//
//            // 4. 构建用户映射
//            Map<Integer, User> userMap = users.stream()
//                    .collect(Collectors.toMap(User::getId, user -> user));
//
//            // 5. 构建提醒数据并发送消息
//            for (OccupiedOrderDTO currentOrder : expiringOrders) {
//                try {
//                    // 发送到期提醒给当前用户
//                    User currentUser = userMap.get(currentOrder.getOrder().getUserId());
//                    if (currentUser != null) {
//                        sendExpiringReminder(currentOrder, currentUser);
//                    }
//
//                    // 查找该车位的下一个预订
//                    List<Order> spotNextOrders = nextOrders.stream()
//                            .filter(order -> order.getParkingSpotId().equals(
//                                    currentOrder.getOccupiedSpot().getParkingSpotId()))
//                            .collect(Collectors.toList());
//
//                    // 如果有下一个预订，发送可能延迟的提醒
//                    if (!CollectionUtils.isEmpty(spotNextOrders)) {
//                        for (Order nextOrder : spotNextOrders) {
//                            User nextUser = userMap.get(nextOrder.getUserId());
//                            if (nextUser != null) {
//                                sendPossibleDelayReminder(currentOrder, nextOrder, nextUser);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    log.error("Failed to send reminder for order: {}", currentOrder.getOrder().getId(), e);
//                }
//            }
//        } catch (Exception e) {
//            log.error("Failed to check orders to remind", e);
//        }
//    }
//
//    /**
//     * 发送到期提醒给当前用户
//     */
//    private void sendExpiringReminder(OccupiedOrderDTO orderDTO, User user) {
//        String endTime = orderDTO.getOccupiedSpot().getEndTime().format(TIME_FORMATTER);
//        String message = String.format(
//                "您的停车订单即将于%s到期，请注意及时驶离车位。如需延长停车时间，请及时续约。",
//                endTime
//        );
//        smsService.sendMessage(user.getPhone(), message);
//        log.info("Sent expiring reminder to user: {}, order: {}", user.getId(), orderDTO.getOrder().getId());
//    }
//
//    /**
//     * 发送可能延迟提醒给下一个预订用户
//     */
//    private void sendPossibleDelayReminder(OccupiedOrderDTO currentOrder, Order nextOrder, User nextUser) {
//        String startTime = nextOrder.getStartTime().format(TIME_FORMATTER);
//        String message = String.format(
//                "您预订的停车位（开始时间%s）当前正在使用中，可能会出现短暂延迟，请耐心等待或取消预约使用其他车位。",
//                startTime
//        );
//        smsService.sendMessage(nextUser.getPhone(), message);
//        log.info("Sent delay reminder to next user: {}, order: {}", nextUser.getId(), nextOrder.getId());
//    }
//
//    @Data
//    static class RemindData {
//        private Order order;
//        private String phone;
//        private String message;
//    }
}