package com.parking.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.enums.parking.ParkingTypeEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.admin.request.AdminParkingRequest;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.UpdateParkingRequest;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.admin.AdminParkingService;
import com.parking.util.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AdminParkingServiceImpl implements AdminParkingService {

    @Resource
    private ParkingSpotRepository parkingSpotRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private AesUtil aesUtil;

    @Override
    public PageResponse<ParkingSpotDTO> getParkingList(AdminParkingRequest request) {
        IPage<ParkingSpot> parkingSpotList = parkingSpotRepository.findParkingSpotList(
                request.getOwnerId(),
                request.getLocation(),
                request.getMaxPrice(), request.getMinPrice(),
                ParkingTypeEnum.getCodeByDescription(request.getParkingType()),
                request.getStatus(),
                request.getPage(), request.getSize());

        return convertToListResponse(parkingSpotList);
    }


    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(Long parkingSpotId) {
        // 获取停车位详情
        ParkingSpot spot = parkingSpotRepository.findById(parkingSpotId);
        if (spot == null) {
            throw new BusinessException("Parking spot not found");
        }

        User owner = userRepository.findById(spot.getOwnerId(), Lists.newArrayList("id", "nick_name", "phone"));
        if (owner == null) {
            throw new BusinessException("Parking spot owner not found");
        }


        DetailResponse<ParkingSpotDetailDTO> detail = convertToDetailResponse(false,
                owner, spot, null, null, null);
        if (detail != null && detail.getData() != null && detail.getData().getOwner() != null) {
            detail.getData().getOwner().setPhone(aesUtil.decrypt(owner.getPhone()));
        }
        return detail;
    }


    @Override
    @Transactional
    public OperationResponse auditParkingSpot(UpdateParkingRequest request) {
        if (ObjectUtils.anyNull(request.getId(), request.getStatus())) {
            throw new BusinessException("Invalid request");
        }

        // 获取车位
        ParkingSpot spot = parkingSpotRepository.findById(
                request.getId(), Lists.newArrayList("id", "owner_id"));
        if (spot == null) {
            throw new ResourceNotFoundException("Parking spot not found");
        }

        // 更新信息
        if (request.getStatus() != null) {
            spot.setStatus(request.getStatus());
        }

        parkingSpotRepository.update(spot);

        return OperationResponse.operationSuccess(spot.getId(), "update success");
    }

}
