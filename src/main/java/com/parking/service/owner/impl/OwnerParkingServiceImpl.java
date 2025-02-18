package com.parking.service.owner.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.OwnerParkingRequest;
import com.parking.model.param.owner.request.UpdateParkingRequest;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.owner.OwnerParkingService;
import com.parking.util.tool.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OwnerParkingServiceImpl implements OwnerParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    public PageResponse<ParkingSpotDTO> getParkingList(Long userId, Integer status, Integer page, Integer size) {
        // 查询车位列表
        IPage<ParkingSpot> pages = parkingSpotRepository.findByOwnerAndStatus(userId, status, page, size);
        return convertToListResponse(pages);
    }

    @Override
    @Transactional
    public OperationResponse createParking(OwnerParkingRequest request) {
        // 创建车位
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setOwnerId(request.getOwnerId());
        parkingSpot.setLocation(request.getLocation());
        parkingSpot.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        parkingSpot.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        parkingSpot.setDescription(request.getDescription());
        parkingSpot.setPrice(request.getPrice());
        parkingSpot.setImages(JsonUtil.toJson(request.getImages()));
        parkingSpot.setRules(JsonUtil.toJson(request.getRules()));
        parkingSpot.setFacilities(JsonUtil.toJson(request.getFacilities()));
        // 默认待审核状态
        parkingSpot.setStatus(SpotStatusEnum.PENDING.getStatus());

        parkingSpotRepository.insert(parkingSpot);

        return OperationResponse.operationSuccess(parkingSpot.getId(), "create success");
    }

    @Override
    @Transactional
    public OperationResponse updateParking(UpdateParkingRequest request) {
        // 获取并验证车位
        ParkingSpot spot = parkingSpotRepository.findById(request.getId());
        if (spot == null) {
            throw new ResourceNotFoundException("Parking spot not found");
        }
        if (!spot.getOwnerId().equals(request.getOwnerId())) {
            throw new BusinessException("Parking spot owner id mismatch");
        }
        
        // 更新信息
        if (request.getLocation() != null) {
            spot.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            spot.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            spot.setPrice(request.getPrice());
        }
        if (request.getImages() != null) {
            spot.setImages(JsonUtil.toJson(request.getImages()));
        }
        if (request.getRules() != null) {
            spot.setRules(JsonUtil.toJson(request.getRules()));
        }
        if (request.getFacilities() != null) {
            spot.setFacilities(JsonUtil.toJson(request.getFacilities()));
        }
        if (request.getStatus() != null) {
            spot.setStatus(request.getStatus());
        }
        
        parkingSpotRepository.update(spot);
        
        return OperationResponse.operationSuccess(spot.getId(), "update success");
    }

    @Override
    public OperationResponse deleteParking(Long id) {
        parkingSpotRepository.delete(id);
        return OperationResponse.operationSuccess(id, "delete success");
    }

}