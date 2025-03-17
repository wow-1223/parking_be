package com.parking.service2.fulfill.impl;

import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.service2.fulfill.FulfillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FulfillServiceImpl implements FulfillService {

    @Override
    public boolean fallLock(Long spotId) {
        return false;
    }

    @Override
    public boolean raiseLock(Long spotId) {
        return false;
    }

    @Override
    public OperationResponse completeOrder(CancelOrderRequest request) {
        return null;
    }
}
