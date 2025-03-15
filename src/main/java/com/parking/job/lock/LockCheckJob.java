package com.parking.job.lock;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.handler.redis.RedisUtil;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.lock.LockService;
import com.parking.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.parking.constant.LockConstant.BREAKDOWN_LOCKS;
import static com.parking.constant.LockConstant.LOCK_ACTION;

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
    private RedisUtil redisUtil;

    @Autowired
    private AesUtil aesUtil;
    @Autowired
    private LockService lockService;


    /**
     * 检测变更了状态的地锁
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void loweredLockCheck() {
        log.info("Start lock check");
        String s = redisUtil.get(LOCK_ACTION);
        if (StringUtils.isBlank(s)) {
            return;
        }

        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> lockActions = JsonUtil.fromJson(s, type);
        List<String> raisedLocks = Lists.newArrayList();
        List<String> loweredLocks = Lists.newArrayList();
        List<String> breakdownLocks = Lists.newArrayList();
        for (String lockAction : lockActions) {
            String status = lockService.getLockStatus(lockAction);
            switch (LockStatusEnum.getEnumByStatus(status)) {
                case RAISED:
                    raisedLocks.add(lockAction);
                    break;
                case LOWERED:
                    loweredLocks.add(lockAction);
                    break;
                default:
                    breakdownLocks.add(lockAction);
            }
        }

        forbiddenBreakdownLock(breakdownLocks);

    }

    /**
     * 禁用故障地锁
     *  1. 识别故障地锁
     *  2. 禁用对应车位
     *  3. 取消对应订单
     *  4. 通知用户与租户
     * 每30秒执行一次
     */
//    @Scheduled(cron = "0/30 * * * * ?")
    public void forbiddenBreakdownLock(List<String> breakdownLocks) {
        log.info("Start forbidden breakdown lock");
        List<ParkingSpot> spots = parkingSpotRepository.findByDeviceIds(breakdownLocks);
        if (CollectionUtils.isEmpty(spots)) {
            log.info("No spots found for device ids: {}", breakdownLocks);
            return;
        }
        Map<Long, ParkingSpot> ownerSpotMap = spots.stream().collect(Collectors.toMap(ParkingSpot::getOwnerId, spot -> spot));

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
            breakdownLocks.forEach(t -> redisUtil.sDel(BREAKDOWN_LOCKS, t));
        } catch (Exception e) {
            log.error("Failed to forbidden breakdown lock", e);
        }
        log.info("Finish forbidden breakdown lock");
    }
}
