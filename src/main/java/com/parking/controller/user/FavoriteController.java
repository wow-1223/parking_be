package com.parking.controller.user;

import com.parking.model.dto.*;
import com.parking.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "收藏接口")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation("收藏/取消收藏")
    @PostMapping
    public OperationResponse toggleFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.toggleFavorite(request);
    }

    @ApiOperation("获取收藏列表")
    @GetMapping
    public PageResponse<ParkingSpotDTO> getFavorites(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return favoriteService.getFavorites(page, pageSize);
    }
} 