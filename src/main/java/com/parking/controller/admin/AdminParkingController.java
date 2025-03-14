package com.parking.controller.admin;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.admin.request.AdminParkingRequest;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.service.admin.AdminParkingService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/parking")
public class AdminParkingController {

    @Resource
    private AdminParkingService adminParkingService;

    /**
     * 管理员获取车位列表
     * @param request 管理员车位列表请求参数
     * @return 车位列表
     */
    @PostMapping("/nearby")
    public PageResponse<ParkingSpotDTO> getNearbyParkings(@RequestBody AdminParkingRequest request) {
        return adminParkingService.getParkingList(request);
    }

    /**
     * 获取车位详情
     * @param  parkingSpotId 车位id
     * @return 车位详情
     */
    @PostMapping("/detail")
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(@RequestParam Long parkingSpotId) {
        return adminParkingService.getParkingDetail(parkingSpotId);
    }

}
