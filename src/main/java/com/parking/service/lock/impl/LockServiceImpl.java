package com.parking.service.lock.impl;

import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.lock.BindLockRequest;
import com.parking.model.param.lock.ControlLockRequest;
import com.parking.model.param.lock.OperateLockRequest;
import com.parking.model.vo.lock.LockVO;
import com.parking.service.lock.LockService;
import org.springframework.stereotype.Service;

@Service
public class LockServiceImpl implements LockService {


    @Override
    public OperationResponse bindLock(BindLockRequest request, String token) {
        return null;
    }

    @Override
    public OperationResponse updateStatus(OperateLockRequest request, String token) {
        return null;
    }

    @Override
    public OperationResponse controlLock(ControlLockRequest request, String token) {
        return null;
    }

    @Override
    public DetailResponse<LockVO> getLockInfo(String deviceId, String token) {
        return null;
    }
}
