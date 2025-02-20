package com.parking.service.user;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.parking.request.ParkingSpotDetailRequest;
import com.parking.service.ParkSpotService;

public interface UserParkingService extends ParkSpotService {


    /**
     * 查询附近的停车位
     * @param request 收藏请求
     * @return 操作结果
     */
    PageResponse<ParkingSpotDTO> getNearbyParkings(NearbyParkingSpotRequest request);

    /**
     * 获取用户收藏的停车位列表
     * @param userId userId
     * @param page 页码
     * @param size 每页大小
     * @return 分页的停车位列表
     */
    PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size);

    /**
     * 获取停车位详情
     * @param request request
     * @return 停车位详情
     */
    DetailResponse<ParkingSpotDetailDTO> getParkingDetail(ParkingSpotDetailRequest request);
} 