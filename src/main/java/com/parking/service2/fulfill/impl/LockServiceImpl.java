package com.parking.service2.fulfill.impl;

import com.parking.service2.fulfill.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LockServiceImpl implements LockService {

    @Override
    public boolean fallLock(Long spotId) {
        return false;
    }

    @Override
    public boolean raiseLock(Long spotId) {
        return false;
    }

}
