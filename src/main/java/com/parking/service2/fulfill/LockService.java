package com.parking.service2.fulfill;

/**
 * 地锁服务
 */
public interface LockService {

    /**
     * 地锁开锁
     */
    boolean fallLock(Long spotId);

    /**
     * 地锁上锁
     */
    boolean raiseLock(Long spotId);

}
