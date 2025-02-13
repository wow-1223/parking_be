package com.parking.controller.owner;

import com.parking.model.dto.common.OperationResponse;
import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.owner.*;
import com.parking.model.dto.owner.request.OwnerParkingRequest;
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
    public OwnerParkingResponse createParking(@RequestBody OwnerParkingRequest request) {
        return ownerParkingService.createParking(request);
    }

    @PostMapping("/updateParking")
    public OperationResponse updateParking(@RequestBody UpdateParkingRequest request) {
        return ownerParkingService.updateParking(request);
    }

    @GetMapping("/getParkingList")
    public PageResponse<OwnerParkingListItemDTO> getParkingList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return ownerParkingService.getParkingList(status, page, pageSize);
    }
} 