package com.parking.service;

import com.parking.model.dto.owner.*;
import com.parking.model.dto.PageResponse;

public interface OwnerParkingService {
    /**
     * 发布车位
     * @param request 车位信息
     * @return 创建结果
     */
    OwnerParkingResponse createParking(CreateParkingRequest request);
    
    /**
     * 修改车位信息
     * @param id 车位ID
     * @param request 更新信息
     * @return 操作结果
     */
    OperationResponse updateParking(String id, UpdateParkingRequest request);
    
    /**
     * 获取车位列表
     * @param status 状态筛选
     * @param page 页码
     * @param pageSize 每页大小
     * @return 车位列表
     */
    PageResponse<OwnerParkingListItemDTO> getParkingList(String status, Integer page, Integer pageSize);
} 