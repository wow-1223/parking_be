package com.parking.controller.user;

import com.parking.model.dto.parking.request.NearbyParkingSpotRequest;
import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.dto.user.response.ParkingDetailResponse;
import com.parking.service.user.UserParkingService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/parking")
public class UserParkingController {

    @Autowired
    private UserParkingService parkingService;

    @GetMapping("/nearby")
    public ParkingListResponse getNearbyParkings(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam Long startTime,
            @RequestParam Long endTime,
            @RequestParam(required = false, defaultValue = "1000") Integer radius,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        NearbyParkingSpotRequest request = new NearbyParkingSpotRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setRadius(radius);
        request.setPage(page);
        request.setSize(pageSize);
        return parkingService.getNearbyParkings(request);
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