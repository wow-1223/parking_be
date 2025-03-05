package com.parking.job;

import com.google.common.collect.Lists;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.service.sms.SmsService;
import com.parking.model.dto.join.OrderUserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ParkingReminderJob {

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private SmsService smsService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 每小时30分执行一次
     */
    @Scheduled(cron = "0 30 * * * ?")
    public void sendParkingReminders() {
        log.info("Start sending parking reminders");
        try {
            // 1. 获取下一个小时的预定记录
            LocalDateTime nextHour = LocalDateTime.now().plusHours(1);
            List<OccupiedSpot> occupiedList = occupiedSpotRepository.findAllByTimeInterval(nextHour, nextHour.plusHours(1));
            if (CollectionUtils.isEmpty(occupiedList)) {
                return;
            }
            Map<Long, OccupiedSpot> occupiedMap = occupiedList.stream()
                    .collect(Collectors.toMap(OccupiedSpot::getId, spot -> spot));

            // 2. 查询占用停车位的地点信息
            List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll
                    (Lists.newArrayList(occupiedMap.keySet()), Lists.newArrayList("id", "location"));
            if (CollectionUtils.isEmpty(parkingSpots)) {
                return;
            }
            Map<Long, ParkingSpot> parkingMap = parkingSpots.stream()
                    .collect(Collectors.toMap(ParkingSpot::getId, spot -> spot));


            // 3. 批量查询订单信息
            List<Long> occupiedIds = occupiedList.stream()
                    .map(OccupiedSpot::getId)
                    .collect(Collectors.toList());
            List<OrderUserDTO> orderUsers = orderRepository
                    .findOrderWithUserByOccupied(occupiedIds, OrderStatusEnum.RESERVED.getStatus());
            if (CollectionUtils.isEmpty(orderUsers)) {
                return;
            }

            // 4. 准备批量发送的消息
            List<String[]> reminderMessages = new ArrayList<>();
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
                reminderMessages.add(new String[]{user.getPhone(), buildReminderMessage(occupiedSpot, parkingSpot)});
            }

            // 5. 批量发送提醒
            batchSendReminders(reminderMessages);

        } catch (Exception e) {
            log.error("Send parking reminders failed", e);
        }
    }

    /**
     * 构建提醒消息
     */
    private String buildReminderMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的停车位(%s)将在%s-%s期间可用，请准时使用。如需取消，请提前操作。距离使用时间前15分钟将自动确定，且无法取消。",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    /**
     * 批量发送提醒消息
     */
    private void batchSendReminders(List<String[]> reminderMessages) {
        if (CollectionUtils.isEmpty(reminderMessages)) {
            return;
        }
        for (String[] phoneAndMessage : reminderMessages) {
            try {
                log.info("Send reminder message to user: {}, message: {}",
                        phoneAndMessage[0], phoneAndMessage[1]);
                smsService.sendMessage( phoneAndMessage[0], phoneAndMessage[1]);
            } catch (Exception e) {
                log.error("Send reminder message failed, phone: {}",  phoneAndMessage[0], e);
            }
        }
    }
}