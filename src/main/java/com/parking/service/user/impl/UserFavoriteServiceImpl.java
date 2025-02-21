package com.parking.service.user.impl;

import com.google.common.collect.Lists;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.entity.mybatis.Favorite;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.repository.mybatis.FavoriteRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.user.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserFavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    @Transactional
    public OperationResponse toggleFavorite(FavoriteRequest request) {
        if (request.getAction()) {
            // 收藏
            Boolean exist = parkingSpotRepository.exist(request.getParkingSpotId());
            if (!exist) {
                throw new ResourceNotFoundException("ParkingSpot not found");
            }

            Favorite favorite = favoriteRepository.exist(null, request.getUserId(), request.getParkingSpotId(), false);
            if (favorite == null) {
                favorite = new Favorite();
                favorite.setUserId(request.getUserId());
                favorite.setParkingSpotId(request.getParkingSpotId());
                favoriteRepository.insert(favorite);
            } else if (favorite.getDeletedAt() != 0) {
                favorite.setDeletedAt(0L);
                favoriteRepository.update(favorite);
            } else {
                throw new BusinessException("Favorite already exists");
            }
            return OperationResponse.operationSuccess(favorite.getId(), "add favorite success");
        } else {
            // 取消收藏
            if (request.getId() == null) {
                throw new BusinessException("favorite id not found");
            }
            Favorite favorite = favoriteRepository.exist(request.getId(), null, null);
            if (favorite == null) {
                throw new ResourceNotFoundException("Favorite not found");
            }
            favoriteRepository.delete(favorite);
            return OperationResponse.operationSuccess(favorite.getId(), "delete favorite success");
        }
    }
} 