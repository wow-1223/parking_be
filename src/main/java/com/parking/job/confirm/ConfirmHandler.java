package com.parking.job.confirm;

import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class ConfirmHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SmsService smsService;

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

    /**
     * 构建提醒消息
     */
    public String buildReminderMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位已自动确认，无法取消。请按时使用，祝您停车愉快！",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }
}
