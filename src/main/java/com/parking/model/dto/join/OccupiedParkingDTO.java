package com.parking.model.dto.join;

import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.ParkingSpot;
import lombok.Data;

@Data
public class OccupiedParkingDTO {
    private OccupiedSpot occupiedSpot;
    private ParkingSpot parkingSpot;
}
