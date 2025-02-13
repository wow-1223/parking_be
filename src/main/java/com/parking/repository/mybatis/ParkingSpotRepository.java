package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.dto.parking.request.NearbyParkingSpotRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ParkingSpotRepository {

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    /**
     * 根据ID查找车位
     */
    public ParkingSpot findById(Long id) {
        return parkingSpotMapper.selectById(id);
    }

    /**
     * 根据停车位ID查找车位
     */
    public List<ParkingSpot> findByParkingSpotId(Long id) {
        return parkingSpotMapper.selectList(new QueryWrapper<ParkingSpot>().eq("id", id));
    }

    /**
     * 根据停车位位置分页查找车位
     */
    public List<ParkingSpot> findByParkingLotId(String location, Integer page, Integer size) {
        return parkingSpotMapper.selectList(new QueryWrapper<ParkingSpot>().like("location", location).last("limit " + page + "," + size));
    }

    /**
     * 根据停车位位置统计数量
     */
    public Long countByParkingSpotId(String location) {
        return parkingSpotMapper.selectCount(new QueryWrapper<ParkingSpot>().like("location", location));
    }

    /**
     * 根据停车位位置查找车位
     */
    public List<ParkingSpot> findByLocation(String location) {
        return parkingSpotMapper.selectList(new QueryWrapper<ParkingSpot>().eq("location", location));
    }

    /**
     * 查找附近可用的停车位
     */
    public IPage<ParkingSpot> findNearbyAvailable(NearbyParkingSpotRequest request) {
        return parkingSpotMapper.selectAvailableParkingSpots(new Page<>(request.getPage(), request.getSize()),
                request.getLatitude(), request.getLongitude(), request.getRadius(),
                new Timestamp(request.getStartTime()), new Timestamp(request.getEndTime()));
    }

    /**
     * 统计附近可用的停车位数量
     */
    public Long countNearbyAvailable(NearbyParkingSpotRequest request) {
        return parkingSpotMapper.countAvailableParkingSpots(
                request.getLatitude(), request.getLongitude(), request.getRadius(),
                new Timestamp(request.getStartTime()), new Timestamp(request.getEndTime()));
    }

    /**
     * 根据停车位信息分页查找车位
     */
    public IPage<ParkingSpot> findByParkingSpotInfo(ParkingSpot parkingSpot, Integer page, Integer size) {
        QueryWrapper<ParkingSpot> query = new QueryWrapper<>();
        if (parkingSpot.getId() != null) {
            query.eq("id", parkingSpot.getId());
        }
        if (parkingSpot.getOwnerId()!= null) {
            query.eq("owner_id", parkingSpot.getOwnerId());
        }
        if (parkingSpot.getLocation()!= null) {
            query.like("location", parkingSpot.getLocation());
        }
        if (parkingSpot.getDescription()!= null) {
            query.like("description", parkingSpot.getDescription());
        }
        if (parkingSpot.getPrice()!= null) {
            query.le("price", parkingSpot.getPrice());
        }
        if (parkingSpot.getRules()!= null) {
            query.eq("rules", parkingSpot.getRules());
        }
        return parkingSpotMapper.selectPage(new Page<>(page, size), query);
    }


    /**
     * 根据停车场ID查找车位
     */
    public List<ParkingSpot> findByParkingLotIdAndStatusAndType(Long parkingLotId, Integer status, Integer type) {
        return parkingSpotMapper.selectList(new QueryWrapper<ParkingSpot>().eq("parking_lot_id", parkingLotId).eq("status", status).eq("type", type));
    }

}
