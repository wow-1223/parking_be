package com.parking.controller.owner;

import com.parking.model.dto.common.OperationResponse;
import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.owner.*;
import com.parking.model.dto.owner.request.CreateParkingRequest;
import com.parking.model.dto.owner.request.UpdateParkingRequest;
import com.parking.model.dto.owner.response.OwnerParkingResponse;
import com.parking.service.owner.OwnerParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/parking")
public class OwnerParkingController {

    @Autowired
    private OwnerParkingService ownerParkingService;

    @PostMapping("/createParking")
    public OwnerParkingResponse createParking(@RequestBody CreateParkingRequest request) {
        return ownerParkingService.createParking(request);
    }

    @PutMapping("/{id}")
    public OperationResponse updateParking(
            @PathVariable String id,
            @RequestBody UpdateParkingRequest request) {
        return ownerParkingService.updateParking(id, request);
    }

    @GetMapping("/getParkingList")
    public PageResponse<OwnerParkingListItemDTO> getParkingList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return ownerParkingService.getParkingList(status, page, pageSize);
    }
} 