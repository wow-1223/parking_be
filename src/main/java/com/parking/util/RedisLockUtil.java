package com.parking.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisLockUtil {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     *
     * @param lockKey 锁的key
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(0, timeout, unit);
        } catch (Exception e) {
            log.error("获取分布式锁失败，lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 加锁（等待获取锁）
     *
     * @param lockKey     锁的key
     * @param waitTime    等待时间
     * @param leaseTime   持有锁的时间
     * @param unit        时间单位
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (Exception e) {
            log.error("获取分布式锁失败，lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，lockKey={}", lockKey, e);
        }
    }

    /**
     * 判断是否已经加锁
     */
    public boolean isLocked(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 判断是否是当前线程持有锁
     */
    public boolean isHeldByCurrentThread(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        return lock.isHeldByCurrentThread();
    }
}