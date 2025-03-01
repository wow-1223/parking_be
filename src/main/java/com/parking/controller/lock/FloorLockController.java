package com.parking.controller.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/lock")
public class FloorLockController {

    @PostMapping("/getLockNumber")
    public static String getLockNumber(String floorNumber) {
        String lockNumber = "";
        return lockNumber;
    }


}
