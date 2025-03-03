package com.parking.service.lock;

import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.lock.BindLockRequest;
import com.parking.model.param.lock.ControlLockRequest;
import com.parking.model.param.lock.OperateLockRequest;
import com.parking.model.vo.lock.LockVO;

public interface LockService {

    OperationResponse bindLock(BindLockRequest request, String token);
    OperationResponse updateStatus(OperateLockRequest request, String token);
    OperationResponse controlLock(ControlLockRequest request, String token);
    DetailResponse<LockVO> getLockInfo(String deviceId, String token);

}
