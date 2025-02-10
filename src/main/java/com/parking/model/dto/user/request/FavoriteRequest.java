package com.parking.model.dto.user.request;

import lombok.Data;

@Data
public class FavoriteRequest {
    private String parkingId;
    private String action; // "add" or "remove"
} 