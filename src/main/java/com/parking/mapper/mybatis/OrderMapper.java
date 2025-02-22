package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.owner.response.StatisticsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    String SELECT_EARNING_SQL = """
            SELECT 
                DATE(create_time) as date,
                SUM(amount) as total_amount,
                COUNT(*) as order_count
            FROM orders 
            WHERE owner_id = #{ownerId}
                AND status = 4
                AND create_time >= #{startDate}
                AND create_time < #{endDate}
                AND deleted_at = 0
            GROUP BY DATE(create_time)
            ORDER BY date ASC
        """;

    String SELECT_PARKING_USAGE_SQL = """
            SELECT
                DATE(create_time) as date,
                SUM(amount) as total_amount,
                COUNT(*) as order_count
            FROM orders
            WHERE owner_id = #{ownerId}
                AND status = 4
                AND parking_spot_id = #{parkingSpotId}
                AND create_time >= #{startDate}  
                AND create_time < #{endDate} 
                AND deleted_at = 0
            GROUP BY DATE(create_time)
            ORDER BY date ASC
        """;

    String SELECT_OVERALL_USAGE_SQL = """
            SELECT
                DATE(create_time) as date,
                SUM(amount) as total_amount,
                COUNT(*) as order_count
            FROM orders
            WHERE owner_id = #{ownerId}
                AND status = 4
                AND create_time >= #{startDate}
                AND create_time < #{endDate} 
                AND deleted_at = 0
            GROUP BY DATE(create_time)
            ORDER BY date ASC
        """;

    @Select(SELECT_EARNING_SQL)
    List<StatisticsResponse> selectEarningsStatistics(@Param("ownerId") Long ownerId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Select(SELECT_PARKING_USAGE_SQL)
    List<StatisticsResponse> selectParkingUsageStatistics(@Param("ownerId") Long ownerId,
                                                          @Param("parkingSpotId") Long parkingSpotId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    @Select(SELECT_OVERALL_USAGE_SQL)
    List<StatisticsResponse> selectOverallUsageStatistics(@Param("ownerId") Long ownerId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

}