package com.parking.controller.owner;

import com.parking.handler.jwt.TokenUtil;
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

/**
 * 租户停车位相关接口
 */
@RestController
@RequestMapping("/api/owner/parking")
public class OwnerParkingController {

    @Autowired
    private OwnerParkingService ownerParkingService;

    /**
     * 创建停车位
     * @param request 创建停车位请求
     * @return 创建停车位结果
     */
    @PostMapping("/createParking")
    public OperationResponse createParking(@RequestBody OwnerParkingRequest request) {
        return ownerParkingService.createParking(request);
    }

    /**
     * 更新停车位
     * @param request 更新停车位请求
     * @return 更新停车位结果
     */
    @PostMapping("/updateParking")
    public OperationResponse updateParking(@RequestBody UpdateParkingRequest request) {
        return ownerParkingService.updateParking(request);
    }

    /**
     * 删除停车位
     * @param request 删除停车位请求
     * @return 删除停车位结果
     */
    @PostMapping("/deleteParking")
    public OperationResponse deleteParking(@RequestBody DeleteParkingRequest request) {
        return ownerParkingService.deleteParking(request);
    }

    /**
     * 获取停车位列表
     * @param status 停车位状态，可选，不传则查询全部
     * @param page 页码，默认1
     * @param size 每页数量，默认20
     * @return 停车位列表
     */
    @GetMapping("/getParkingList")
    public PageResponse<ParkingSpotDTO> getParkingList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ownerParkingService.getParkingList(TokenUtil.getUserId(), status, page, size);
    }

    /**
     * 获取停车位详情
     * @param parkingSpotId 停车位id
     * @return 停车位详情
     */
    @GetMapping("/getParkingDetail")
    public DetailResponse<ParkingSpotDetailDTO> getParkingList(
            @RequestParam Long parkingSpotId) {
        return ownerParkingService.getParkingDetail(TokenUtil.getUserId(), parkingSpotId);
    }
} 