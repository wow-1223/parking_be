package com.parking.service.user.impl;

import com.parking.exception.ResourceNotFoundException;
import com.parking.model.entity.mybatis.Favorite;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.repository.mybatis.FavoriteRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.user.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    @Transactional
    public OperationResponse toggleFavorite(FavoriteRequest request) {

        Boolean exist = parkingSpotRepository.exist(request.getParkingSpotId());
        if (!exist) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }

        if (request.getAction()) {
            // 收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(request.getUserId());
            favorite.setParkingSpotId(request.getParkingSpotId());
            favoriteRepository.insert(favorite);

            return OperationResponse.operationSuccess(favorite.getId(), "add favorite success");
        } else {
            // 取消收藏
            favoriteRepository.delete(request.getId());
            return OperationResponse.operationSuccess(request.getId(), "delete favorite success");
        }
    }
} 