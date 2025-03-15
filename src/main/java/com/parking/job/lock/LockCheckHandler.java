package com.parking.job.lock;

import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class LockCheckHandler {

    @Autowired
    private SmsService smsService;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Transactional(rollbackFor = Exception.class)
    public void batchForbiddenSpots(List<ParkingSpot> spots, List<String[]> messages) {
        try {
            if (CollectionUtils.isNotEmpty(spots)) {
                // 批量更新车位状态
                parkingSpotRepository.batchUpdate(spots);
            }

            // 批量发送确认消息
            for (String[] phoneAndMessage : messages) {
                try {
                    log.info("Send remind message to owner: {}, message: {}",
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

    public String buildOwnerCheckMessage(ParkingSpot parkingSpot) {
        return String.format(
                "您出租的的停车位[%s]地锁被损坏，现已被自动冻结，无法接收新订单。请尽快确认车位情况并至小程序重新提交审核，避免影响用户使用。",
                parkingSpot.getLocation()
        );
    }
}
