package com.parking.controller.user;

import com.parking.handler.jwt.TokenUtil;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.FavoriteService;


import com.parking.service.user.UserParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏相关接口
 */
@RestController
@RequestMapping("/api/user/favorites")
public class UserFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserParkingService userParkingService;

    /**
     * 收藏/取消收藏
     * @param request 收藏请求
     * @return 收藏结果
     */
    @PostMapping("/toggleFavorite")
    public OperationResponse toggleFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.toggleFavorite(request);
    }

    /**
     * 获取用户收藏列表
     * @param page 页码
     * @param size 每页数量
     * @return 用户收藏列表
     */
    @GetMapping("/getFavorites")
    public PageResponse<ParkingSpotDTO> getFavorites(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return userParkingService.getFavorites(TokenUtil.getUserId(), page, size);
    }
} 