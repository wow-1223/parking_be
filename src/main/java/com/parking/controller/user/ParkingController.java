package com.parking.controller.user;

import com.parking.model.dto.*;
import com.parking.service.ParkingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "停车位接口")
@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @ApiOperation("获取附近停车位")
    @GetMapping("/nearby")
    public ParkingListResponse getNearbyParkings(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return parkingService.getNearbyParkings(latitude, longitude, radius, page, pageSize);
    }

    @ApiOperation("搜索停车位")
    @GetMapping("/search")
    public ParkingListResponse searchParkings(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return parkingService.searchParkings(keyword, page, pageSize);
    }

    @ApiOperation("获取停车位详情")
    @GetMapping("/{id}")
    public ParkingDetailResponse getParkingDetail(@PathVariable String id) {
        return parkingService.getParkingDetail(id);
    }
} 