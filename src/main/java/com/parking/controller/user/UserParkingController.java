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

/**
 * 用户查询车位相关接口
 */
@RestController
@RequestMapping("/api/user/parking")
public class UserParkingController {

    @Autowired
    private UserParkingService parkingService;

    /**
     * 获取附近车位
     * @param request 附近车位请求参数
     * @return 附近车位列表
     */
    @PostMapping("/nearby")
    public PageResponse<ParkingSpotDTO> getNearbyParkings(@RequestBody NearbyParkingSpotRequest request) {
        return parkingService.getNearbyParkings(request);
    }

    /**
     * 获取车位详情
     * @param request 车位详情请求参数
     * @return 车位详情
     */
    @PostMapping("/detail")
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(@RequestBody ParkingSpotDetailRequest request) {
        return parkingService.getParkingDetail(request);
    }
} 