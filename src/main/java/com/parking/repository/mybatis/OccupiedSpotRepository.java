package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.mapper.mybatis.ParkingOccupiedMapper;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OccupiedSpotRepository {

    @Autowired
    private ParkingOccupiedMapper parkingOccupiedMapper;

    /**
     * 新增车位占用
     */
    public void insert(OccupiedSpot occupiedSpot) {
        parkingOccupiedMapper.insert(occupiedSpot);
    }

    /**
     * 更新车位
     */
    public void update(OccupiedSpot occupiedSpot) {
        parkingOccupiedMapper.updateById(occupiedSpot);
    }

    public OccupiedSpot findById(Long id) {
        return parkingOccupiedMapper.selectById(id);
    }

    public List<OccupiedSpot> findByDay(Long spotId, String parkingDay) {
        return parkingOccupiedMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("parking_spot_id", spotId)
                        .eq("parking_day", parkingDay)
                        .eq("deleted_at", 0L));
    }

    public List<OccupiedSpot> findByTime(Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        return parkingOccupiedMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("parking_spot_id", spotId)
                        .ge("start_time", startTime)
                        .le("end_time", endTime)
                        .eq("deleted_at", 0L));
    }

    public List<OccupiedSpot> findAll(List<Long> ids, List<String> selectFields) {
        return parkingOccupiedMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .in("id", ids)
                        .eq("deleted_at", 0L)
                        .select(selectFields));
    }
}
