package com.parking.controller.lock;

import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.lock.BindLockRequest;
import com.parking.model.param.lock.ControlLockRequest;
import com.parking.model.param.lock.OperateLockRequest;
import com.parking.model.vo.lock.LockVO;
import com.parking.service.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/lock")
public class GroundLockController {

    private static final String AUTHORIZATION = "Authorization Lock";

    @Autowired
    private LockService lockService;

    @PostMapping("/bind")
    public OperationResponse bindLock(@RequestBody BindLockRequest request,
                                      @RequestHeader(AUTHORIZATION) String token) {
        log.info("Binding lock request: {}", request);
        return lockService.bindLock(request, token);
    }

    @PostMapping("/status")
    public OperationResponse updateStatus(
            @RequestBody OperateLockRequest request,
            @RequestHeader(AUTHORIZATION) String token) {
        log.info("Update lock status: {}", request);
        return lockService.updateStatus(request, token);
    }

    @PostMapping("/control")
    public OperationResponse controlLock(
            @RequestBody ControlLockRequest request,
            @RequestHeader(AUTHORIZATION) String token) {
        log.info("Control lock request: {}", request);
        return lockService.controlLock(request, token);
    }

    @GetMapping("/{deviceId}")
    public DetailResponse<LockVO> getLockInfo(
            @PathVariable String deviceId,
            @RequestHeader(AUTHORIZATION) String token) {
        log.info("Get lock info for device: {}", deviceId);
        return lockService.getLockInfo(deviceId, token);
    }

}
