package com.parking.service;

import com.parking.model.dto.FavoriteRequest;
import com.parking.model.dto.OperationResponse;
import com.parking.model.dto.PageResponse;
import com.parking.model.dto.ParkingSpotDTO;

public interface FavoriteService {
    /**
     * 收藏/取消收藏停车位
     * @param request 收藏请求
     * @return 操作结果
     */
    OperationResponse toggleFavorite(FavoriteRequest request);
    
    /**
     * 获取用户收藏的停车位列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页的停车位列表
     */
    PageResponse<ParkingSpotDTO> getFavorites(Integer page, Integer pageSize);
} 