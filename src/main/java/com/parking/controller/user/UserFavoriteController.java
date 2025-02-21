package com.parking.controller.user;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.FavoriteService;


import com.parking.service.user.UserParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/favorites")
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
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return userParkingService.getFavorites(userId, page, size);
    }
} 