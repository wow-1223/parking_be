package com.parking.controller.user;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.service.user.UserParkingService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
public class UserParkingController {

    @Autowired
    private UserParkingService parkingService;

    @PostMapping("/nearby")
    public PageResponse<ParkingSpotDTO> getNearbyParkings(@RequestBody NearbyParkingSpotRequest request) {
        return parkingService.getNearbyParkings(request);
    }

    @GetMapping("/{id}/{startTime}/{endTime}")
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(
            @PathVariable String id,
            @PathVariable String startTime,
            @PathVariable String endTime) {
        return parkingService.getParkingDetail(id, startTime, endTime);
    }
} 