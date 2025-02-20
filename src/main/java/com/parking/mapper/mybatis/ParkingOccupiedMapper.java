package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ParkingOccupiedMapper extends BaseMapper<OccupiedSpot> {

    String GET_OCCUPIED_SPOT_ID_LIST = "SELECT parking_spots_id " +
            "FROM parking_occupied " +
            "WHERE parking_spots_id in (${spotIds}) " +
            "AND parking_day = #{parkingDay} " +
            "AND ((start_time >= #{startTime} AND start_time <= #{endTime}) OR (end_time >= #{startTime} AND end_time <= #{endTime})) " +
            "AND deleted_at = 0 ";

    /**
     * 根据停车位ID与时间区间查找车位占用记录
     */
    @Select(GET_OCCUPIED_SPOT_ID_LIST)
    List<Long> getParkingSpotIdByTimeInterval(@Param("spotIds") String spotIds,
                                              @Param("parkingDay") LocalDate parkingDay,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
}
