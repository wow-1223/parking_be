package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class FavoriteRequest {

    private Long id;

    private Long userId;

    private Long parkingSpotId;

    // "add" or "remove"
    private Boolean action;
}