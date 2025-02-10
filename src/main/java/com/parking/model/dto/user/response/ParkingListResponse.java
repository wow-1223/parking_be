package com.parking.model.dto.user.response;

import com.parking.model.dto.ParkingSpotDTO;
import lombok.Data;
import java.util.List;

@Data
public class ParkingListResponse {
    private long total;
    private List<ParkingSpotDTO> list;
} 