package com.parking.controller.owner;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
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

    @PostMapping("/deleteParking/{id}")
    public OperationResponse deleteParking(@PathVariable("id") Long id) {
        return ownerParkingService.deleteParking(id);
    }

    @GetMapping("/getParkingList")
    public PageResponse<ParkingSpotDTO> getParkingList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ownerParkingService.getParkingList(userId, status, page, size);
    }
} 