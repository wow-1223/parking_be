package com.parking.job.remind;

import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class RemindHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SmsService smsService;

    @Transactional(rollbackFor = Exception.class)
    public void batchRemindOrders(List<Order> orders, List<String[]> confirmMessages) {
        try {
            if (CollectionUtils.isNotEmpty(orders)) {
                // 批量更新订单状态
                orderRepository.batchUpdate(orders);
            }

            // 批量发送确认消息
            for (String[] phoneAndMessage : confirmMessages) {
                try {
                    log.info("Send remind message to user: {}, message: {}",
                            phoneAndMessage[0], phoneAndMessage[1]);
                    smsService.sendMessage(phoneAndMessage[0], phoneAndMessage[1]);
                } catch (Exception e) {
                    log.error("Send remind message failed, phone: {}",  phoneAndMessage[0], e);
                }
            }
        } catch (Exception e) {
            log.error("Batch remind orders failed", e);
            throw e; // 触发事务回滚
        }
    }

    /**
     * 构建提醒消息
     */
    public String buildConfirmMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位已自动确认，取消将扣取一定费用。请按时使用，祝您停车愉快！",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    public String buildUserOccupiedWithoutOtherAvailableSpotMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位正在被使用，可能产生延迟，可至平台选择取消或与当前用户沟通",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    public String buildUserOccupiedWithOtherAvailableSpotMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位正在被使用，可能产生延迟。已为您找到最近的其他可用车位，请至小程序确认是否确认更换",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    public String buildUnknownOccupiedWithoutOtherAvailableSpotsMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位被未知用户占用，可能无法使用。请前往小程序选择取消、继续等待或与租户沟通",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    public String buildUnknownOccupiedWithOtherAvailableSpotsMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位被未知用户占用，可能无法使用。已为您找到目的地附近最近的其他可用车位，请至小程序确认是否更换",
                parkingSpot.getLocation(),
                startTime,
                endTime
        );
    }

    public String buildOwnerCheckMessage(ParkingSpot parkingSpot) {
        return String.format(
                "您出租的的停车位[%s]因未知原因被占用，请尽快确认车位情况并至小程序提交审核，避免影响用户使用",
                parkingSpot.getLocation()
        );
    }

    public String buildWillEndOrderRemindMessage(OccupiedSpot occupiedSpot, ParkingSpot parkingSpot) {
        String startTime = occupiedSpot.getStartTime().format(TIME_FORMATTER);
        String endTime = occupiedSpot.getEndTime().format(TIME_FORMATTER);
        return String.format(
                "您预订的%s(%s-%s)停车位即将到期，请在%s时间前离开或继续预约延长使用时间，超时将面临高额罚款",
                parkingSpot.getLocation(),
                startTime,
                endTime,
                endTime
        );
    }
}
