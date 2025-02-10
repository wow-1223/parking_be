package com.parking.controller.user;

import com.parking.model.dto.user.request.FavoriteRequest;
import com.parking.model.dto.ParkingSpotDTO;
import com.parking.model.dto.common.OperationResponse;
import com.parking.model.dto.common.PageResponse;
import com.parking.service.user.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户收藏接口")
@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation("收藏/取消收藏")
    @PostMapping("/toggleFavorite")
    public OperationResponse toggleFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.toggleFavorite(request);
    }

    @ApiOperation("获取收藏列表")
    @GetMapping("/getFavorites")
    public PageResponse<ParkingSpotDTO> getFavorites(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return favoriteService.getFavorites(page, pageSize);
    }
} 