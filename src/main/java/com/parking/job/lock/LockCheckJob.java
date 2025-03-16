package com.parking.job.lock;

import com.google.common.collect.Lists;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LockCheckJob {

    @Autowired
    private LockCheckHandler lockCheckHandler;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AesUtil aesUtil;

    @Autowired
    private LockService lockService;


//    /**
//     *
//     *  故障：通知租户
//     * 每30秒执行一次
//     */
//    @Scheduled(cron = "0/30 * * * * ?")
//    public void lockCheck() {
//        log.info("Start lock check");
//        forbiddenBrokenLock();
//    }

    /**
     * 禁用故障地锁
     *  1. 识别故障地锁
     *  2. 禁用对应车位
     *  3. 取消对应订单
     *  4. 通知用户与租户
     * 每30秒执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void forbiddenBrokenLock() {
        log.info("Start forbidden broken locks");
        List<String> brokenLocks = lockService.getBrokenLocks();
        List<ParkingSpot> spots = parkingSpotRepository.findByDeviceIds(brokenLocks);
        if (CollectionUtils.isEmpty(spots)) {
            log.info("No spots found for device ids: {}", brokenLocks);
            return;
        }
        Map<Long, ParkingSpot> ownerSpotMap = spots.stream().collect(
                Collectors.toMap(ParkingSpot::getOwnerId, spot -> spot));

        List<Long> ownerIds = spots.stream().map(ParkingSpot::getOwnerId).toList();
        List<User> owners = userRepository.findByIds(ownerIds, Lists.newArrayList("id", "phone"));

        List<String[]> ownerMessages = Lists.newArrayListWithCapacity(owners.size());
        for (User user : owners) {
            ParkingSpot spot = ownerSpotMap.get(user.getId());
            spot.setStatus(SpotStatusEnum.FORBIDDEN.getStatus());
            String phone = aesUtil.decrypt(user.getPhone());
            String message = lockCheckHandler.buildOwnerCheckMessage(spot);
            ownerMessages.add(new String[]{phone, message});
        }
        try {
            lockCheckHandler.batchForbiddenSpots(spots, ownerMessages);
            lockService.deleteBrokenLocks(brokenLocks);
//            brokenLocks.forEach(t -> redisUtil.sDel(BREAKDOWN_LOCKS, t));
        } catch (Exception e) {
            log.error("Failed to forbidden broken lock", e);
        }
        log.info("Finish forbidden broken lock");
    }
}
