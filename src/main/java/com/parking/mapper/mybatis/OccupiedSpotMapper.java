package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.model.dto.join.OccupiedOrderDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OccupiedSpotMapper extends BaseMapper<OccupiedSpot> {

    String GET_OCCUPIED_SPOTS_BY_TIME_INTERVAL = """
                SELECT #{fields}
                FROM parking_occupied
                WHERE parking_spot_id in (${spotIds})
                AND parking_day = #{parkingDay}
                AND ((start_time >= #{startTime} AND start_time <= #{endTime}) OR (end_time >= #{startTime} AND end_time <= #{endTime}))
                AND deleted_at = 0
            """;

    String FIND_TIMEOUT_SPOTS_WITH_ORDERS = """
                SELECT * 
                FROM parking_occupied po
                INNER JOIN orders o ON o.parking_occupied_id = po.id
                WHERE po.end_time <= #{checkTime}
                AND o.status in (${orderStatus})
                AND po.deleted_at = 0
                AND o.deleted_at = 0
            """;

    /**
     * 根据停车位ID与时间区间查找占用信息
     */
    @Select(GET_OCCUPIED_SPOTS_BY_TIME_INTERVAL)
    List<OccupiedSpot> getOccupiedSpotsByTimeInterval(@Param("fields") String fields,
                                                      @Param("spotIds") String spotIds,
                                                      @Param("parkingDay") LocalDate parkingDay,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 联表查询即将超时的订单和占用信息
     */
    @Select(FIND_TIMEOUT_SPOTS_WITH_ORDERS)
    List<OccupiedOrderDTO> findTimeoutSpotsWithOrders(
            @Param("checkTime") LocalDateTime checkTime,
            @Param("orderStatus") String orderStatus);
}
