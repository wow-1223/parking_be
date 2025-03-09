package com.parking.service.lock.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LockServiceImpl implements LockService {

    private static final String LOCK_SHARD_KEY = "lock.shard";
    private static final String LOCK_KEY = "lock:";
    private static final String BREAKDOWN_LOCKs = "breakdown:locks";

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
        String lockStatus = redisUtil.hGet(LOCK_SHARD_KEY, getLockKey(request.getDeviceId()));
        if (lockStatus == null || !Objects.equals(request.getStatus(), lockStatus)) {
            redisUtil.hSet(LOCK_SHARD_KEY, getLockKey(request.getDeviceId()), lockStatus);
        }
        if (LockStatusEnum.UNKNOWN.getStatus().equals(lockStatus)) {
            redisUtil.sAdd(BREAKDOWN_LOCKs, request.getDeviceId());
        }
        return OperationResponse.operationSuccess(0L, "");
    }

    @Override
    public OperationResponse controlLock(ControlLockRequest request, String token) {

        return null;
    }

    @Override
    public DetailResponse<LockVO> getLockInfo(String deviceId, String token) {
        String lockStatus = getLockStatus(deviceId);
        if (lockStatus == null) {
            throw new ResourceNotFoundException("Lock not exist");
        }

        LockVO lock = new LockVO();
        lock.setDeviceId(deviceId);
        lock.setStatus(lockStatus);
        return DetailResponse.detailSuccess(lock, "getLockInfo success");
    }

    @Override
    public String getLockStatus(String deviceId) {
        return redisUtil.hGet(LOCK_KEY, getLockKey(deviceId));
    }

    @Override
    public Map<String, String> getLockStatus(List<String> deviceIds) {
        return redisUtil.hMultiGet(LOCK_KEY, deviceIds);
    }

    private String getLockKey(String deviceId) {
        return LOCK_KEY + deviceId;
    }
}
