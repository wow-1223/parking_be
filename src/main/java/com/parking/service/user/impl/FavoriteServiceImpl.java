package com.parking.service.impl;

import com.parking.common.exception.BusinessException;
import com.parking.common.exception.ResourceNotFoundException;
import com.parking.model.dto.*;
import com.parking.model.entity.Favorite;
import com.parking.model.entity.ParkingSpot;
import com.parking.model.entity.User;
import com.parking.repository.FavoriteRepository;
import com.parking.repository.ParkingSpotRepository;
import com.parking.service.FavoriteService;
import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    @Transactional
    public OperationResponse toggleFavorite(FavoriteRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Long parkingId = Long.parseLong(request.getParkingId());
        
        // 验证停车位是否存在
        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("停车位不存在"));
        
        if ("add".equals(request.getAction())) {
            // 检查是否已收藏
            if (favoriteRepository.existsByUserIdAndParkingSpotId(currentUser.getId(), parkingId)) {
                throw new BusinessException("该停车位已收藏");
            }
            
            // 创建收藏记录
            Favorite favorite = new Favorite();
            favorite.setUser(currentUser);
            favorite.setParkingSpot(parkingSpot);
            favorite.setCreateTime(LocalDateTime.now());
            favoriteRepository.save(favorite);
            
            return OperationResponse.success("收藏成功");
        } else if ("remove".equals(request.getAction())) {
            // 查找并删除收藏记录
            Favorite favorite = favoriteRepository.findByUserIdAndParkingSpotId(currentUser.getId(), parkingId)
                    .orElseThrow(() -> new BusinessException("未找到收藏记录"));
            
            favoriteRepository.delete(favorite);
            return OperationResponse.success("取消收藏成功");
        } else {
            throw new BusinessException("不支持的操作类型");
        }
    }

    @Override
    public PageResponse<ParkingSpotDTO> getFavorites(Integer page, Integer pageSize) {
        User currentUser = securityUtil.getCurrentUser();
        
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(
                currentUser.getId(),
                PageRequest.of(page - 1, pageSize)
        );
        
        // 转换为DTO
        List<ParkingSpotDTO> parkingSpots = favoritePage.getContent().stream()
                .map(favorite -> convertToDTO(favorite.getParkingSpot()))
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                favoritePage.getTotalElements(),
                parkingSpots
        );
    }
    
    private ParkingSpotDTO convertToDTO(ParkingSpot parkingSpot) {
        ParkingSpotDTO dto = new ParkingSpotDTO();
        dto.setId(parkingSpot.getId().toString());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLatitude(parkingSpot.getLatitude());
        dto.setLongitude(parkingSpot.getLongitude());
        dto.setPrice(parkingSpot.getPrice());
        dto.setImage(parkingSpot.getImage());
        dto.setStatus(parkingSpot.getStatus());
        return dto;
    }
} 