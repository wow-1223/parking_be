package com.parking.service.owner;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.OwnerParkingRequest;
import com.parking.model.param.owner.request.UpdateParkingRequest;
import com.parking.service.ParkSpotService;

public interface OwnerParkingService extends ParkSpotService {

    /**
     * 获取车位列表
     * @param userId userId
     * @param status 状态筛选
     * @param page 页码
     * @param size 每页大小
     * @return 车位列表
     */
    PageResponse<ParkingSpotDTO> getParkingList(Long userId, Integer status, Integer page, Integer size);

    /**
     * 发布车位
     * @param request 车位信息
     * @return 创建结果
     */
    OperationResponse createParking(OwnerParkingRequest request);
    
    /**
     * 修改车位信息
     * @param request 更新信息
     * @return 操作结果
     */
    OperationResponse updateParking(UpdateParkingRequest request);

    /**
     * 删除车位
     * @param id 车位ID
     * @return 操作结果
     */
    OperationResponse deleteParking(Long id);
} 