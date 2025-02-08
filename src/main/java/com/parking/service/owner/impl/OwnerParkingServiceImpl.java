package com.parking.service.impl;

import com.parking.common.exception.BusinessException;
import com.parking.common.exception.ResourceNotFoundException;
import com.parking.model.dto.owner.*;
import com.parking.model.dto.PageResponse;
import com.parking.model.entity.ParkingSpot;
import com.parking.model.entity.User;
import com.parking.repository.ParkingSpotRepository;
import com.parking.service.OwnerParkingService;
import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerParkingServiceImpl implements OwnerParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    @Transactional
    public OwnerParkingResponse createParking(CreateParkingRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        
        // 创建车位
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setOwner(currentUser);
        parkingSpot.setLocation(request.getLocation());
        parkingSpot.setLatitude(request.getLatitude());
        parkingSpot.setLongitude(request.getLongitude());
        parkingSpot.setDescription(request.getDescription());
        parkingSpot.setPrice(request.getPrice());
        parkingSpot.setImages(request.getImages());
        parkingSpot.setRules(request.getRules());
        parkingSpot.setFacilities(request.getFacilities());
        parkingSpot.setStatus("pending"); // 默认待审核状态
        
        parkingSpotRepository.save(parkingSpot);
        
        // 返回响应
        OwnerParkingResponse response = new OwnerParkingResponse();
        response.setId(parkingSpot.getId().toString());
        response.setStatus(parkingSpot.getStatus());
        
        return response;
    }

    @Override
    @Transactional
    public OperationResponse updateParking(String id, UpdateParkingRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        
        // 获取并验证车位
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ResourceNotFoundException("车位不存在"));
                
        if (!parkingSpot.getOwner().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权修改此车位");
        }
        
        // 更新信息
        if (request.getLocation() != null) {
            parkingSpot.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            parkingSpot.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            parkingSpot.setPrice(request.getPrice());
        }
        if (request.getImages() != null) {
            parkingSpot.setImages(request.getImages());
        }
        if (request.getRules() != null) {
            parkingSpot.setRules(request.getRules());
        }
        if (request.getFacilities() != null) {
            parkingSpot.setFacilities(request.getFacilities());
        }
        
        parkingSpotRepository.save(parkingSpot);
        
        return OperationResponse.success("更新成功");
    }

    @Override
    public PageResponse<OwnerParkingListItemDTO> getParkingList(String status, Integer page, Integer pageSize) {
        User currentUser = securityUtil.getCurrentUser();
        
        // 查询车位列表
        Page<ParkingSpot> parkingPage;
        if ("all".equals(status) || status == null) {
            parkingPage = parkingSpotRepository.findByOwnerId(
                    currentUser.getId(),
                    PageRequest.of(page - 1, pageSize)
            );
        } else {
            parkingPage = parkingSpotRepository.findByOwnerIdAndStatus(
                    currentUser.getId(),
                    status,
                    PageRequest.of(page - 1, pageSize)
            );
        }
        
        // 转换为DTO
        List<OwnerParkingListItemDTO> parkingList = parkingPage.getContent().stream()
                .map(this::convertToListItemDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(parkingPage.getTotalElements(), parkingList);
    }
    
    private OwnerParkingListItemDTO convertToListItemDTO(ParkingSpot parkingSpot) {
        OwnerParkingListItemDTO dto = new OwnerParkingListItemDTO();
        dto.setId(parkingSpot.getId().toString());
        dto.setLocation(parkingSpot.getLocation());
        dto.setStatus(parkingSpot.getStatus());
        
        // 如果有当前订单，设置订单信息
        if (parkingSpot.getCurrentOrder() != null) {
            OwnerParkingListItemDTO.CurrentOrder currentOrder = 
                    new OwnerParkingListItemDTO.CurrentOrder();
            currentOrder.setId(parkingSpot.getCurrentOrder().getId().toString());
            currentOrder.setEndTime(parkingSpot.getCurrentOrder().getEndTime().toString());
            dto.setCurrentOrder(currentOrder);
        }
        
        return dto;
    }
} 