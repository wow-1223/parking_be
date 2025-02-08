package com.parking.model.dto;

import lombok.Data;

@Data
public class FavoriteRequest {
    private String parkingId;
    private String action; // "add" or "remove"
} 