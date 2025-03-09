package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.parking.mapper.mybatis.ParkingSpotMapper;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ParkingSpotRepository {

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;


    /**
     * 新增车位
     */
    public void insert(ParkingSpot parkingSpot) {
        parkingSpotMapper.insert(parkingSpot);
    }

    /**
     * 更新车位
     */
    public void update(ParkingSpot parkingSpot) {
        parkingSpotMapper.updateById(parkingSpot);
    }

    public void batchUpdate(List<ParkingSpot> parkingSpots) {
        parkingSpotMapper.updateById(parkingSpots);
    }

    /**
     * 删除车位
     */
    public void delete(Long id) {
        ParkingSpot spot = findById(id, Lists.newArrayList("id"));
        if (spot == null) {
            throw new RuntimeException("Parking spot not found");
        }
        spot.setDeletedAt(DateUtil.getCurrentTimestamp());
        update(spot);
    }

    /**
     * 判断车位是否存在
     */
    public Boolean exist(Long id) {
        return parkingSpotMapper.selectCount(new QueryWrapper<ParkingSpot>().eq("id", id).eq("deleted_at", 0L)) > 0;
    }

    /**
     * 根据ID查找车位
     */
    public ParkingSpot findById(Long id) {
        return parkingSpotMapper.selectOne(new QueryWrapper<ParkingSpot>().eq("id", id).eq("deleted_at", 0L));
    }

    /**
     * 根据ID查找车位指定字段
     */
    public ParkingSpot findById(Long id, List<String> fields) {
        return parkingSpotMapper.selectOne(
                new QueryWrapper<ParkingSpot>()
                       .eq("id", id)
                       .eq("deleted_at", 0L)
                       .select(fields));
    }

    /**
     * 根据ID列表查找车位指定信息
     */
    public IPage<ParkingSpot> findByPage(List<Long> ids, List<String> selectFields, Integer page, Integer size) {
        return parkingSpotMapper.selectPage(new Page<>(page, size),
                new QueryWrapper<ParkingSpot>()
                        .in("id", ids)
                        .eq("deleted_at", 0L)
                        .orderByDesc("update_time")
                        .select(selectFields));
    }

    public IPage<ParkingSpot> findFavoriteParkingSpots(Long userId, Integer page, Integer size) {
        return parkingSpotMapper.getFavoriteParkingSpots(new Page<>(page, size), userId);
    }

    /**
     * 根据ID列表查找车位指定信息
     */
    public List<ParkingSpot> findAll(List<Long> ids, List<String> selectFields) {
        return parkingSpotMapper.selectList(
                new QueryWrapper<ParkingSpot>()
                        .in("id", ids)
                        .eq("deleted_at", 0L)
                        .orderByDesc("update_time")
                        .select(selectFields));
    }

    public List<ParkingSpot> findAvailableParkingSpotIdList(
            Double longitude, Double latitude, Integer radius, BigDecimal price) {
        return parkingSpotMapper.getAvailableParkingSpots(
                longitude, latitude, radius, price);
    }

    public List<ParkingSpot> findOtherAvailableParkingSpotIdList(List<Long> spotIds, Double longitude, Double latitude,
                                                                 Integer radius, BigDecimal price) {
        return parkingSpotMapper.getOtherAvailableParkingSpots(
                StringUtils.join(spotIds, ","), longitude, latitude, radius, price);
    }

    /**
     * 根据owner与status查找车位
     * @param ownerId ownerId
     * @param status status
     * @param page page
     * @param size size
     * @return List<ParkingSpot>
     */
    public IPage<ParkingSpot> findByOwnerAndStatus(Long ownerId, Integer status, Integer page, Integer size) {
        QueryWrapper<ParkingSpot> query = new QueryWrapper<>();
        if (ownerId != null) {
            query.eq("owner_id", ownerId);
        }
        if (status != null) {
            query.eq("status", status);
        }
        query.eq("deleted_at", 0L);
        query.orderByDesc("update_time");
        return parkingSpotMapper.selectPage(new Page<>(page, size), query);
    }

    public List<ParkingSpot> findByDeviceIds(List<String> deviceIds) {
        return parkingSpotMapper.selectList(new QueryWrapper<ParkingSpot>()
                .in("device_id", deviceIds).eq("deleted_at", 0L));
    }
}
