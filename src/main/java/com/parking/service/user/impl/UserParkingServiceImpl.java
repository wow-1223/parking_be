package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.ParkingSpotDTO;
import com.parking.model.dto.parking.request.NearbyParkingSpotRequest;
import com.parking.model.dto.user.response.ParkingDetailResponse;
import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.user.UserParkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户停车位服务实现
 */
@Slf4j
@Service
public class UserParkingServiceImpl implements UserParkingService {

//    @Autowired
//    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    public ParkingListResponse getNearbyParkings(NearbyParkingSpotRequest request) {
        // 查询附近可用的停车位
        IPage<ParkingSpot> iPage = parkingSpotRepository.findNearbyAvailable(request);
        iPage.getRecords().forEach(parkingSpot -> {
            log.info("parkingSpot: {}", parkingSpot);

        })
        List<ParkingSpotDTO> spots = parkingPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        ParkingListResponse response = new ParkingListResponse();
        response.setTotal(parkingPage.getTotalElements());
        response.setList(spots);
        return response;
    }

    @Override
    public ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize) {
        // 搜索停车位
        Page<ParkingSpot> parkingPage = parkingSpotRepository.search(
                keyword, PageRequest.of(page - 1, pageSize));

        List<ParkingSpotDTO> spots = parkingPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        ParkingListResponse response = new ParkingListResponse();
        response.setTotal(parkingPage.getTotalElements());
        response.setList(spots);
        return response;
    }

    @Override
    public ParkingDetailResponse getParkingDetail(String id) {
        // 获取停车位详情
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSpot", "id", id));

        return convertToDetailResponse(parkingSpot);
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
        dto.setStatus(parkingSpot.getStatus());
        return dto;
    }

    /**
     * 转换为详情响应
     */
    private ParkingDetailResponse convertToDetailResponse(ParkingSpot parkingSpot) {
        ParkingDetailResponse response = new ParkingDetailResponse();
        response.setId(parkingSpot.getId().toString());
        response.setLocation(parkingSpot.getLocation());
        response.setDescription(parkingSpot.getDescription());
        response.setImages(parkingSpot.getImages());
        response.setPrice(parkingSpot.getPrice());
        response.setStatus(parkingSpot.getStatus());
        response.setRules(parkingSpot.getRules());
        response.setFacilities(parkingSpot.getFacilities());

        // 设置所有者信息
        ParkingDetailResponse.OwnerInfo ownerInfo = new ParkingDetailResponse.OwnerInfo();
        ownerInfo.setId(parkingSpot.getOwner().getId().toString());
        ownerInfo.setName(parkingSpot.getOwner().getNickName());
        ownerInfo.setPhone(parkingSpot.getOwner().getPhone());
        ownerInfo.setRating(calculateOwnerRating(parkingSpot.getOwner().getId()));
        response.setOwner(ownerInfo);

        return response;
    }

    /**
     * 计算车位所有者评分
     */
    private Double calculateOwnerRating(Long ownerId) {
        // TODO: 实现评分计算逻辑
        return 4.5;
    }
}