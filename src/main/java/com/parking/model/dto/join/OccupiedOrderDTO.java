package com.parking.model.dto.join;

import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import lombok.Data;

@Data
public class OccupiedOrderDTO {
    private OccupiedSpot occupiedSpot;
    private Order order;
}