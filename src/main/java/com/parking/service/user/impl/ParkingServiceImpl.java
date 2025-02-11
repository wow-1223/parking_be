package com.parking.service.user.impl;

import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.*;
import com.parking.model.entity.ParkingSpot;
import com.parking.repository.ParkingSpotRepository;
import com.parking.service.user.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    public ParkingListResponse getNearbyParkings(Double latitude, Double longitude, 
            Integer radius, Integer page, Integer pageSize) {
        // 默认搜索半径1公里
        radius = radius == null ? 1000 : radius;
        
        Page<ParkingSpot> parkingPage = parkingSpotRepository.findNearbyAvailable(
                longitude, 
                latitude, 
                radius,
                PageRequest.of(page - 1, pageSize)
        );
        
        return convertToListResponse(parkingPage);
    }

    @Override
    public ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize) {
        Page<ParkingSpot> parkingPage = parkingSpotRepository.search(
                keyword,
                PageRequest.of(page - 1, pageSize)
        );
        
        return convertToListResponse(parkingPage);
    }

    @Override
    public ParkingDetailResponse getParkingDetail(String id) {
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ResourceNotFoundException("停车位不存在"));
        
        ParkingDetailResponse response = new ParkingDetailResponse();
        response.setId(parkingSpot.getId().toString());
        response.setLocation(parkingSpot.getLocation());
        response.setDescription(parkingSpot.getDescription());
        response.setImages(parkingSpot.getImages());
        response.setPrice(parkingSpot.getPrice());
        response.setStatus(parkingSpot.getStatus());
        response.setRules(parkingSpot.getRules());
        response.setFacilities(parkingSpot.getFacilities());
        
        // 设置车位所有者信息
        ParkingDetailResponse.OwnerInfo ownerInfo = new ParkingDetailResponse.OwnerInfo();
        ownerInfo.setId(parkingSpot.getOwner().getId().toString());
        ownerInfo.setName(parkingSpot.getOwner().getNickName());
        ownerInfo.setPhone(parkingSpot.getOwner().getPhone());
        // TODO: 计算车位所有者的平均评分
        ownerInfo.setRating(4.5);
        
        response.setOwner(ownerInfo);
        
        return response;
    }
    
    private ParkingListResponse convertToListResponse(Page<ParkingSpot> parkingPage) {
        List<ParkingSpotDTO> parkingSpots = parkingPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        ParkingListResponse response = new ParkingListResponse();
        response.setTotal(parkingPage.getTotalElements());
        response.setList(parkingSpots);
        
        return response;
    }
    
    private ParkingSpotDTO convertToDTO(ParkingSpot parkingSpot) {
        ParkingSpotDTO dto = new ParkingSpotDTO();
        dto.setId(parkingSpot.getId().toString());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLatitude(parkingSpot.getLatitude());
        dto.setLongitude(parkingSpot.getLongitude());
        dto.setPrice(parkingSpot.getPrice());
        dto.setImages(parkingSpot.getImages());
        dto.setStatus(parkingSpot.getStatus());
        return dto;
    }
} 