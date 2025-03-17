package com.parking.service2.search;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.parking.request.ParkingSpotDetailRequest;

/**
 * 搜索服务
 */
public interface SearchService {

    /**
     * 查询附近的停车位
     */
    PageResponse<ParkingSpotDTO> getNearbyParkings(NearbyParkingSpotRequest request);

    /**
     * 获取停车位详情
     */
    DetailResponse<ParkingSpotDetailDTO> getParkingDetail(ParkingSpotDetailRequest request);

    /**
     * 获取用户收藏的停车位列表
     */
    PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size);

}
