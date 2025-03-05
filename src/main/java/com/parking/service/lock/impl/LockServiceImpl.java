package com.parking.service.lock.impl;

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

import java.util.Objects;

@Service
public class LockServiceImpl implements LockService {

    private static final String LOCK_SHARD_KEY = "lock.shard";
    private static final String LOCK_KEY = "lock:";

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
        String lockStatus = (String) redisUtil.hGet(LOCK_SHARD_KEY, getLockKey(request.getDeviceId()));
        if (lockStatus == null || !Objects.equals(request.getStatus(), lockStatus)) {
            redisUtil.hSet(LOCK_SHARD_KEY, getLockKey(request.getDeviceId()), lockStatus);
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
        return (String) redisUtil.hGet(LOCK_KEY, getLockKey(deviceId));
    }

    private String getLockKey(String deviceId) {
        return LOCK_KEY + deviceId;
    }
}
