package com.parking.controller.user;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.parking.request.ParkingSpotDetailRequest;
import com.parking.service.user.UserParkingService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/parking")
public class UserParkingController {

    @Autowired
    private UserParkingService parkingService;

    @PostMapping("/nearby")
    public PageResponse<ParkingSpotDTO> getNearbyParkings(@RequestBody NearbyParkingSpotRequest request) {
        return parkingService.getNearbyParkings(request);
    }

    @PostMapping("/detail")
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(@RequestBody ParkingSpotDetailRequest request) {
        return parkingService.getParkingDetail(request);
    }
} 