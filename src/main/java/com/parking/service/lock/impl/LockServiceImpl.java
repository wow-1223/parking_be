package com.parking.service.lock.impl;

import com.google.common.reflect.TypeToken;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.exception.ResourceNotFoundException;
import com.parking.handler.redis.RedisUtil;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.lock.BindLockRequest;
import com.parking.model.param.lock.ControlLockRequest;
import com.parking.model.param.lock.OperateLockRequest;
import com.parking.model.vo.lock.LockVO;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.lock.LockService;
import com.parking.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.parking.constant.LockConstant.*;

@Service
public class LockServiceImpl implements LockService {

    private static final Long LOCK_ACTION_EXPIRE_TIME = 60L;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    public OperationResponse bindLock(BindLockRequest request, String token) {
        ParkingSpot spot = parkingSpotRepository.findById(Long.valueOf(request.getParkingSpotId()));
        if (spot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }
        if (StringUtils.isNotBlank(spot.getDeviceId())) {
            throw new ResourceNotFoundException("ParkingSpot already bind");
        }
        spot.setDeviceId(request.getDeviceId());
        parkingSpotRepository.update(spot);
        return null;
    }

    @Override
    public OperationResponse updateStatus(OperateLockRequest request, String token) {
        String status = redisUtil.get(lockStatusKey(request.getDeviceId()));
        if (status == null || !Objects.equals(request.getStatus(), status)) {
            // 保存地锁状态至redis，不过期
            redisUtil.set(lockStatusKey(request.getDeviceId()), request.getStatus());
        }
        if (Objects.equals(LockStatusEnum.BROKEN.getStatus(), request.getStatus())) {
            // 记录损坏的地锁，过期时间为60s
            redisUtil.sAdd(BROKEN_LOCKS, request.getDeviceId());
            redisUtil.expire(BROKEN_LOCKS, LOCK_ACTION_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        return OperationResponse.operationSuccess(0L, "");
    }

    @Override
    public OperationResponse controlLock(ControlLockRequest request, String token) {
        return null;
    }

    @Override
    public DetailResponse<LockVO> getLockInfo(String deviceId, String token) {
        String status = getLockStatus(deviceId);
        if (status == null) {
            throw new ResourceNotFoundException("Lock not exist");
        }

        LockVO lock = new LockVO();
        lock.setDeviceId(deviceId);
        lock.setStatus(status);
        return DetailResponse.detailSuccess(lock, "getLockInfo success");
    }

    @Override
    public String getLockStatus(String deviceId) {
        return redisUtil.get(lockStatusKey(deviceId));
    }

    @Override
    public Map<String, String> getLockStatus(List<String> deviceIds) {
        Map<String, String> map = new HashMap<>();
        List<String> results = redisUtil.mGet(lockStatusKeys(deviceIds));
        for (int i = 0; i < deviceIds.size(); i++) {
            map.put(deviceIds.get(i), results.get(i));
        }
        return map;
    }

    @Override
    public List<String> getBrokenLocks() {
        String s = redisUtil.get(BROKEN_LOCKS);
        if (StringUtils.isBlank(s)) {
            return null;
        }

        Type type = new TypeToken<List<String>>(){}.getType();
        return JsonUtil.fromJson(s, type);
    }

    @Override
    public void deleteBrokenLocks(List<String> deviceIds) {
        redisUtil.sDel(BROKEN_LOCKS, brokenLockKeys(deviceIds));
    }

    private String lockStatusKey(String deviceId) {
        return LOCK_STATUS_KEY + deviceId;
    }

    private List<String> lockStatusKeys(List<String> deviceIds) {
        return deviceIds.stream().map(t -> LOCK_STATUS_KEY + t).collect(Collectors.toList());
    }

    private String brokenLockKey(String deviceId) {
        return BROKEN_LOCKS + deviceId;
    }

    private List<String> brokenLockKeys(List<String> deviceIds) {
        return deviceIds.stream().map(t -> BROKEN_LOCKS + t).collect(Collectors.toList());
    }
}
