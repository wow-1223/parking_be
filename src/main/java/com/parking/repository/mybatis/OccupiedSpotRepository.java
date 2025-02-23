package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.mapper.mybatis.OccupiedSpotMapper;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OccupiedSpotRepository {

    @Autowired
    private OccupiedSpotMapper occupiedSpotMapper;

    /**
     * 新增车位占用
     */
    public void insert(OccupiedSpot occupiedSpot) {
        occupiedSpotMapper.insert(occupiedSpot);
    }

    /**
     * 更新车位
     */
    public void update(OccupiedSpot occupiedSpot) {
        occupiedSpotMapper.updateById(occupiedSpot);
    }

    public OccupiedSpot findById(Long id) {
        return occupiedSpotMapper.selectById(id);
    }

    public OccupiedSpot findById(Long id, List<String> fields) {
        return occupiedSpotMapper.selectOne(
                new QueryWrapper<OccupiedSpot>().eq("id", id).eq("deleted_at", 0L).select(fields));
    }

    public List<OccupiedSpot> findById(List<Long> idList, List<String> fields) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>().in("id", idList).eq("deleted_at", 0L).select(fields));
    }

    /**
     * 查询指定时间范围内的预定记录
     */
    public List<OccupiedSpot> findReservedByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("status", "RESERVED")
                        .ge("start_time", startTime)
                        .lt("start_time", endTime)
                        .eq("deleted_at", 0)
        );
    }

    /**
     * 查询指定时间开始的预定记录
     */
    public List<OccupiedSpot> findReservedByStartTime(LocalDateTime startTime) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("deleted_at", 0)
                        // 查找开始时间等于指定时间的记录
                        .apply("DATE_FORMAT(start_time,'%Y-%m-%d %H:%i') = DATE_FORMAT({0},'%Y-%m-%d %H:%i')", startTime)
        );
    }

    /**
     * 查询超时的占用记录
     */
    public List<OccupiedOrderDTO> findTimeoutSpotsWithOrders(LocalDateTime checkTime, Integer timeout, Integer orderStatus) {
        return occupiedSpotMapper.findTimeoutSpotsWithOrders(checkTime, timeout, orderStatus);
    }

    public List<OccupiedSpot> findByDay(Long spotId, LocalDate parkingDay) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("parking_spot_id", spotId)
                        .eq("parking_day", parkingDay)
                        .eq("deleted_at", 0));
    }


    public List<OccupiedSpot> findByTime(Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        return occupiedSpotMapper.getParkingSpotsByTimeInterval(spotId, startTime, endTime);
    }

    public List<OccupiedSpot> findAll(List<Long> ids, List<String> selectFields) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .in("id", ids)
                        .eq("deleted_at", 0L)
                        .select(selectFields));
    }

    public List<Long> findParkingSpotIdByTimeInterval(String spotIds, LocalDate parkingDay,
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        return occupiedSpotMapper.getParkingSpotIdByTimeInterval(spotIds, parkingDay, startTime, endTime);
    }
}
