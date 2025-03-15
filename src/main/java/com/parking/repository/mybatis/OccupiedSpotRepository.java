package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.parking.mapper.mybatis.OccupiedSpotMapper;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<OccupiedSpot> findAll(List<Long> ids, List<String> selectFields) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>().in("id", ids).eq("deleted_at", 0L).select(selectFields));
    }

    /**
     * 查询指定时间范围内的占用记录
     */
    public List<OccupiedSpot> findAllByTimeInterval(LocalDateTime startTime, LocalDateTime endTime) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .ge("start_time", startTime)
                        .lt("start_time", endTime)
                        .eq("deleted_at", 0)
        );
    }

    /**
     * 查询指定时间开始的占用记录
     */
    public List<OccupiedSpot> findByStartTime(LocalDateTime startTime) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("deleted_at", 0)
                        // 查找开始时间等于指定时间的记录
                        .apply("DATE_FORMAT(start_time,'%Y-%m-%d %H:%i') = DATE_FORMAT({0},'%Y-%m-%d %H:%i')", startTime)
        );
    }

    /**
     * 查询指定时间结束的占用记录
     */
    public List<OccupiedSpot> findByEndTime(LocalDateTime endTime) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("deleted_at", 0)
                        // 查找开始时间等于指定时间的记录
                        .apply("DATE_FORMAT(end_time,'%Y-%m-%d %H:%i') = DATE_FORMAT({0},'%Y-%m-%d %H:%i')", endTime)
        );
    }

    public List<OccupiedSpot> findByParkingSpot(List<Long> spotIds) {
        return occupiedSpotMapper.selectList(new QueryWrapper<OccupiedSpot>()
                .in("parking_spot_id", spotIds).eq("deleted_at", 0L));
    }

    /**
     * 查询指定日期的全部预定记录
     */
    public List<OccupiedSpot> findByDay(Long spotId, LocalDate parkingDay) {
        return occupiedSpotMapper.selectList(
                new QueryWrapper<OccupiedSpot>()
                        .eq("parking_spot_id", spotId)
                        .eq("parking_day", parkingDay)
                        .eq("deleted_at", 0));
    }

    /**
     * 校验指定车位、指定时间范围内是否存在占用记录
     */
    public Boolean checkExist(Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        List<OccupiedSpot> id = occupiedSpotMapper.getOccupiedSpotsByTimeInterval(
                "id", spotId.toString(), startTime.toLocalDate(), startTime, endTime);
        return CollectionUtils.isNotEmpty(id);
    }

    /**
     * 查询指定车位、指定时间范围内的占用车位ID
     */
    public List<Long> findParkingSpotIdByTimeInterval(List<Long> spotIds,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        List<OccupiedSpot> occupiedSpots = findOccupiedSpotsByTimeInterval(
                Lists.newArrayList("id, parking_spot_id"), spotIds, startTime, endTime);
        return occupiedSpots.stream().map(OccupiedSpot::getId).collect(Collectors.toList());
    }

    private List<OccupiedSpot> findOccupiedSpotsByTimeInterval(List<String> fields,
                                                              List<Long> spotIds,
                                                              LocalDateTime startTime,
                                                              LocalDateTime endTime) {
        return occupiedSpotMapper.getOccupiedSpotsByTimeInterval(StringUtils.join(fields, ","),
                StringUtils.join(spotIds, ","), startTime.toLocalDate(), startTime, endTime);
    }

    /**
     * 查询超时的订单
     */
    public List<OccupiedOrderDTO> findTimeoutSpotsWithOrders(LocalDateTime checkTime, List<Integer> orderStatus) {
        return occupiedSpotMapper.findTimeoutSpotsWithOrders(checkTime, StringUtils.join(orderStatus, ","));
    }
}
