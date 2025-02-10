package com.parking.service.user;

import com.parking.model.dto.ParkingListResponse;
import com.parking.model.dto.ParkingDetailResponse;

public interface ParkingService {
    /**
     * 获取附近的停车位
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 搜索半径（米）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 停车位列表
     */
    ParkingListResponse getNearbyParkings(Double latitude, Double longitude, 
            Integer radius, Integer page, Integer pageSize);
    
    /**
     * 搜索停车位
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 停车位列表
     */
    ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize);
    
    /**
     * 获取停车位详情
     * @param id 停车位ID
     * @return 停车位详细信息
     */
    ParkingDetailResponse getParkingDetail(String id);
} 