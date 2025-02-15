package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.parking.model.dto.ParkingSpotDTO;
import com.parking.model.dto.parking.request.NearbyParkingSpotRequest;
import com.parking.model.dto.user.response.ParkingDetailResponse;
import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.user.UserParkingService;
import com.parking.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户停车位服务实现
 */
@Slf4j
@Service
public class UserParkingServiceImpl implements UserParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    public ParkingListResponse getNearbyParkings(NearbyParkingSpotRequest request) {
        // 查询附近可用的停车位
        IPage<ParkingSpot> iPage = parkingSpotRepository.findNearbyAvailable(request);
        return convertToListResponse(iPage);
    }

    @Override
    public ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize) {
        // 搜索停车位
        IPage<ParkingSpot> iPage = parkingSpotRepository.findByParkingLocation(
                keyword, page, pageSize);
        return convertToListResponse(iPage);
    }

    @Override
    public ParkingDetailResponse getParkingDetail(String id) {
        // 获取停车位详情
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(id));
        return convertToDetailResponse(parkingSpot);
    }

    private ParkingListResponse convertToListResponse(IPage<ParkingSpot> iPage) {
        ParkingListResponse response = new ParkingListResponse();
        if (iPage == null || iPage.getRecords() == null || iPage.getRecords().isEmpty()) {
            response.setTotal(0L);
            return response;
        }

        List<ParkingSpotDTO> spots = iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.setTotal(iPage.getTotal());
        response.setList(spots);
        return response;
    }

    /**
     * 转换为详情响应
     */
    private ParkingDetailResponse convertToDetailResponse(ParkingSpot parkingSpot) {
        ParkingDetailResponse response = new ParkingDetailResponse();
        response.setId(parkingSpot.getId().toString());
        response.setLocation(parkingSpot.getLocation());
        response.setDescription(parkingSpot.getDescription());

//        response.setImages(JsonUtil.fromJson(parkingSpot.getImages(), List.class));
        response.setImages(JsonUtil.fromListJson(parkingSpot.getImages(), String.class));



        response.setPrice(parkingSpot.getPrice());
//        response.setStatus(parkingSpot.getStatus());
//        response.setRules(parkingSpot.getRules());
        response.setRules(JsonUtil.fromListJson(parkingSpot.getRules(), String.class));
//        response.setFacilities(parkingSpot.getFacilities());
        response.setFacilities(JsonUtil.fromListJson(parkingSpot.getFacilities(), String.class));

        // 设置所有者信息
//        ParkingDetailResponse.OwnerInfo ownerInfo = new ParkingDetailResponse.OwnerInfo();
//        ownerInfo.setId(parkingSpot.getOwner().getId().toString());
//        ownerInfo.setName(parkingSpot.getOwner().getNickName());
//        ownerInfo.setPhone(parkingSpot.getOwner().getPhone());
//        ownerInfo.setRating(calculateOwnerRating(parkingSpot.getOwner().getId()));
//        response.setOwner(ownerInfo);

        return response;
    }

    /**
     * 转换为列表DTO
     */
    private ParkingSpotDTO convertToDTO(ParkingSpot parkingSpot) {
        ParkingSpotDTO dto = new ParkingSpotDTO();
        dto.setId(parkingSpot.getId().toString());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLatitude(parkingSpot.getLatitude());
        dto.setLongitude(parkingSpot.getLongitude());
        dto.setPrice(parkingSpot.getPrice());
//        dto.setStatus(parkingSpot.getStatus());
        return dto;
    }

    /**
     * 计算车位所有者评分
     */
    private Double calculateOwnerRating(Long ownerId) {
        // TODO: 实现评分计算逻辑
        return 4.5;
    }
}