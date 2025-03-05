package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class FavoriteRequest {

    /**
     * 收藏id
     */
    private Long id;

    /**
     * 收藏车位id
     */
    private Long parkingSpotId;

    /**
     * 收藏车位动作 true-收藏 false-取消收藏
     */
    // "add" or "remove"
    private Boolean action;
}