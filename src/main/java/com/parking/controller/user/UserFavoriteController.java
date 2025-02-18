package com.parking.controller.user;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.FavoriteService;


import com.parking.service.user.UserOrderService;
import com.parking.service.user.UserParkingService;
import com.parking.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserParkingService userParkingService;

    @PostMapping("/toggleFavorite")
    public OperationResponse toggleFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.toggleFavorite(request);
    }

    @GetMapping("/getFavorites")
    public PageResponse<ParkingSpotDTO> getFavorites(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return userParkingService.getFavorites(userId, page, pageSize);
    }
} 