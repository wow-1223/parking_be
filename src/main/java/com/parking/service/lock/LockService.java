package com.parking.service.lock;

import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.lock.BindLockRequest;
import com.parking.model.param.lock.ControlLockRequest;
import com.parking.model.param.lock.OperateLockRequest;
import com.parking.model.vo.lock.LockVO;

import java.util.List;
import java.util.Map;

public interface LockService {

    OperationResponse bindLock(BindLockRequest request, String token);
    OperationResponse updateStatus(OperateLockRequest request, String token);
    OperationResponse controlLock(ControlLockRequest request, String token);
    DetailResponse<LockVO> getLockInfo(String deviceId, String token);

    String getLockStatus(String deviceId);
    Map<String, String> getLockStatus(List<String> deviceIds);

    /**
     * 获取变更了状态的地锁
     */
    List<String> getBrokenLocks();

    void deleteBrokenLocks(List<String> deviceIds);

}
