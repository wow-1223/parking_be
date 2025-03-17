package com.parking.service2.fulfill;

import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;

/**
 * 履约
 */
public interface FulfillService {

    /**
     * 地锁开锁
     */
    boolean fallLock(Long spotId);

    /**
     * 地锁上锁
     */
    boolean raiseLock(Long spotId);

    /**
     * 完成订单
     */
    OperationResponse completeOrder(CancelOrderRequest request);
}
