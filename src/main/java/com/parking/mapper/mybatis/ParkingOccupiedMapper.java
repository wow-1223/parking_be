package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ParkingOccupiedMapper extends BaseMapper<OccupiedSpot> {

    /**
     * 根据停车位ID与时间区间查找车位占用记录
     */
    @Select("SELECT id " +
            "FROM parking_occupied " +
            "WHERE parking_spots_id in #{parkingSpotIdList} " +
            "AND start_time <= #{endTime} " +
            "AND end_time >= #{startTime} " +
            "AND deleted_at = 0 ")
    List<Long> getParkingSpotIdByTimeInterval(@Param("parkingSpotIdList") List<Long> parkingSpotIds,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
}
