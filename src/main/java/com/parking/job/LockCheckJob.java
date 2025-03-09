package com.parking.job;

import com.google.common.reflect.TypeToken;
import com.parking.handler.redis.RedisUtil;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Component
public class LockCheckJob {

    private static final String BREAKDOWN_LOCKS = "breakdown:locks";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    /**
     * 地锁状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void breakdownLockForbidden() {
        log.info("Start forbidden breakdown lock");
        String s = redisUtil.get(BREAKDOWN_LOCKS);
        if (StringUtils.isBlank(s)) {
            return;
        }
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> breakdownLocks = JsonUtil.fromJson(s, type);
        List<ParkingSpot> spots = parkingSpotRepository.findByDeviceIds(breakdownLocks);
//        List<Long> spotIds = spots.stream().map(ParkingSpot::getId).toList();
//        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findByParkingSpot(spots.stream().map(ParkingSpot::getId).toList());
//        orderRepository.findOrderWithUserByOccupied()



        breakdownLocks.forEach(t -> redisUtil.sDel(BREAKDOWN_LOCKS, t));
    }
}
