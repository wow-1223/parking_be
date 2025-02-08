package com.parking.controller.owner;

import com.parking.model.dto.owner.*;
import com.parking.model.dto.PageResponse;
import com.parking.service.OwnerParkingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "出租方车位管理接口")
@RestController
@RequestMapping("/api/owner/parking")
public class OwnerParkingController {

    @Autowired
    private OwnerParkingService ownerParkingService;

    @ApiOperation("发布车位")
    @PostMapping
    public OwnerParkingResponse createParking(@RequestBody CreateParkingRequest request) {
        return ownerParkingService.createParking(request);
    }

    @ApiOperation("修改车位信息")
    @PutMapping("/{id}")
    public OperationResponse updateParking(
            @PathVariable String id,
            @RequestBody UpdateParkingRequest request) {
        return ownerParkingService.updateParking(id, request);
    }

    @ApiOperation("获取车位列表")
    @GetMapping
    public PageResponse<OwnerParkingListItemDTO> getParkingList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return ownerParkingService.getParkingList(status, page, pageSize);
    }
} 