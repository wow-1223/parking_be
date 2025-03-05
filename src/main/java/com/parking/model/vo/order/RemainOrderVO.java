package com.parking.model.vo.order;

import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.vo.lock.LockVO;
import lombok.Data;

import java.util.Map;

@Data
public class RemainOrderVO {
    private Map<Long, Order> orderMap;
    private Map<Long, ParkingSpot> parkingSpotMap;
    private Map<Long, OccupiedSpot> occupiedSpotMap;
    private Map<String, LockVO> lockMap;
}
