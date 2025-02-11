package com.parking.controller.user;

import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.dto.user.response.ParkingDetailResponse;
import com.parking.service.user.UserParkingService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
public class UserParkingController {

    @Autowired
    private UserParkingService parkingService;

    @GetMapping("/nearby")
    public ParkingListResponse getNearbyParkings(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return parkingService.getNearbyParkings(latitude, longitude, radius, page, pageSize);
    }

    @GetMapping("/search")
    public ParkingListResponse searchParkings(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return parkingService.searchParkings(keyword, page, pageSize);
    }

    @GetMapping("/{id}")
    public ParkingDetailResponse getParkingDetail(@PathVariable String id) {
        return parkingService.getParkingDetail(id);
    }
} 