package com.parking.controller.owner;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.DeleteParkingRequest;
import com.parking.model.param.owner.request.OwnerParkingRequest;
import com.parking.model.param.owner.request.UpdateParkingRequest;
import com.parking.service.owner.OwnerParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/parking")
public class OwnerParkingController {

    @Autowired
    private OwnerParkingService ownerParkingService;

    @PostMapping("/createParking")
    public OperationResponse createParking(@RequestBody OwnerParkingRequest request) {
        return ownerParkingService.createParking(request);
    }

    @PostMapping("/updateParking")
    public OperationResponse updateParking(@RequestBody UpdateParkingRequest request) {
        return ownerParkingService.updateParking(request);
    }

    @PostMapping("/deleteParking")
    public OperationResponse deleteParking(@RequestBody DeleteParkingRequest request) {
        return ownerParkingService.deleteParking(request);
    }

    @GetMapping("/getParkingList")
    public PageResponse<ParkingSpotDTO> getParkingList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ownerParkingService.getParkingList(userId, status, page, size);
    }

    @GetMapping("/getParkingDetail")
    public DetailResponse<ParkingSpotDetailDTO> getParkingList(
            @RequestParam Long userId,
            @RequestParam Long parkingSpotId) {
        return ownerParkingService.getParkingDetail(userId, parkingSpotId);
    }
} 