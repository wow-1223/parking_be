package com.parking.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParkingListResponse {
    private long total;
    private List<ParkingSpotDTO> list;
} 