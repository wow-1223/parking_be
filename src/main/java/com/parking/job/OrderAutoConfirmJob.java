package com.parking.job;

import com.google.common.collect.Lists;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.model.dto.join.OrderUserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.handler.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单自动确认
 *  1. 每分钟执行一次，检查15分钟后开始的预定
 *  2. 批量查询订单信息
 *  3. 过滤出待确认的订单
 *  4. 批量查询用户信息
 *  5. 构建映射关系，方便后续使用
 *  6. 批量处理订单确认
 *  7. 批量更新订单
 *  8. 批量发送确认消息
 */
@Slf4j
@Component
public class OrderAutoConfirmJob {

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SmsService smsService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 每分钟执行一次，检查15分钟后开始的预定
     */
    @Scheduled(cron = "0 * * * * ?")
    public void autoConfirmOrders() {
        log.info("Start auto confirming orders");
        try {
            // 1. 获取15分钟后的时间点
            LocalDateTime confirmTime = LocalDateTime.now().plusMinutes(15);

            // 2. 查询即将开始的预定记录
            List<OccupiedSpot> occupiedList = occupiedSpotRepository.findReservedByStartTime(confirmTime);
            if (CollectionUtils.isEmpty(occupiedList)) {
                return;
            }
            Map<Long, OccupiedSpot> occupiedMap = occupiedList.stream()
                    .collect(Collectors.toMap(OccupiedSpot::getId, spot -> spot));

            // 3. 查询占用停车位的地点信息
            List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll
                    (Lists.newArrayList(occupiedMap.keySet()), Lists.newArrayList("id", "location"));
            if (CollectionUtils.isEmpty(parkingSpots)) {
                return;
            }
            Map<Long, ParkingSpot> parkingMap = parkingSpots.stream()
                    .collect(Collectors.toMap(ParkingSpot::getId, spot -> spot));

            // 4. 批量查询订单信息
            List<Long> occupiedIds = occupiedList.stream()
                    .map(OccupiedSpot::getId)
                    .collect(Collectors.toList());
            List<OrderUserDTO> orderUsers = orderRepository
                    .findOrderWithUserByOccupied(StringUtils.join(occupiedIds), OrderStatusEnum.RESERVED.getStatus());
            if (CollectionUtils.isEmpty(orderUsers)) {
                return;
            }

            // 7. 批量处理订单确认
            List<Order> updatedOrders = new ArrayList<>();

            List<String[]> confirmMessages = new ArrayList<>();
            for (OrderUserDTO ou : orderUsers) {
                User user = ou.getUser();
                Order order = ou.getOrder();
                if (Objects.isNull(order) || Objects.isNull(user) || StringUtils.isBlank(user.getPhone())) {
                    continue;
                }

                OccupiedSpot occupiedSpot = occupiedMap.get(order.getOccupiedSpotId());
                if (occupiedSpot == null) {
                    continue;
                }
                ParkingSpot parkingSpot = parkingMap.get(occupiedSpot.getParkingSpotId());
                if (parkingSpot == null) {
                    continue;
                }

                confirmMessages.add(new String[]{user.getPhone(), buildReminderMessage(occupiedSpot, parkingSpot)});
                order.setStatus(OrderStatusEnum.CONFIRMED.getStatus());
                updatedOrders.add(order);
            }

            // 8. 批量更新订单
            if (!updatedOrders.isEmpty()) {
                batchConfirmOrders(updatedOrders, confirmMessages);
            }
        } catch (Exception e) {
            log.error("Auto confirm orders failed", e);
        }
    }

    /**
     * 构建提醒消息
     */
    private String buildReminderMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位已自动确认，无法取消。请按时使用，祝您停车愉快！",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    /**
     * 批量确认订单并发送消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchConfirmOrders(List<Order> orders, List<String[]> confirmMessages) {
        try {
            // 批量更新订单状态
            orderRepository.batchUpdate(orders);

            // 批量发送确认消息
            for (String[] phoneAndMessage : confirmMessages) {
                try {
                    log.info("Send confirm message to user: {}, message: {}",
                            phoneAndMessage[0], phoneAndMessage[1]);
                    smsService.sendMessage( phoneAndMessage[0], phoneAndMessage[1]);
                } catch (Exception e) {
                    log.error("Send confirm message failed, phone: {}",  phoneAndMessage[0], e);
                }
            }
        } catch (Exception e) {
            log.error("Batch confirm orders failed", e);
            throw e; // 触发事务回滚
        }
    }
}