package com.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.model.entity.mybatis.ParkingSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Mapper
public interface ParkingSpotMapper extends BaseMapper<ParkingSpot> {

    /**
     * 查找附近可用的停车位
     * @param page      分页参数
     * @param latitude  纬度
     * @param longitude 经度
     * @param radius    半径（单位：千米）
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 附近可用的停车位列表
     *
     * SQL 语句部分：
     * 使用 JOIN 关键字将 parking_spots 表（简称 ps）和 parking_periods 表（简称 pp）进行连接，连接条件是 ps.id = pp.parking_spots_id。
     * ST_Distance_Sphere 函数用于计算查询点（传入的经纬度）和停车位经纬度之间的球面距离，单位为米，所以将 radius（千米）乘以 1000 进行转换，筛选出距离在指定半径内的停车位。
     * pp.status = 3 筛选出停车时段状态为可用（status = available）的记录。
     * #{startTime} >= pp.start_time AND #{endTime} <= pp.end_time 用于判断传入的时间段与已有停车时段不冲突，即该停车位在传入的时间段内有空闲。
     * GROUP BY ps.id 按停车位 ID 进行分组，确保每个停车位只出现一次。
     * ORDER BY ST_Distance_Sphere(...) 按距离远近对查询结果进行排序。
     */
    @Select("SELECT ps.* " +
            "FROM parking_spots ps " +
            "LEFT JOIN parking_periods pp ON ps.id = pp.parking_spots_id " +
            "WHERE ST_Distance_Sphere( " +
            "    POINT(#{longitude}, #{latitude}), " +
            "    POINT(ps.longitude, ps.latitude) " +
            ") <= #{radius} " +
            "AND pp.status = 3 " +
            "AND #{startTime} >= pp.start_time AND #{endTime} <= pp.end_time " +
            "GROUP BY ps.id " +
            "ORDER BY ST_Distance_Sphere( " +
            "    POINT(#{longitude}, #{latitude}), " +
            "    POINT(ps.longitude, ps.latitude) " +
            ")")
    IPage<ParkingSpot> selectAvailableParkingSpots(Page<ParkingSpot> page,
                                                   @Param("latitude") BigDecimal latitude,
                                                   @Param("longitude") BigDecimal longitude,
                                                   @Param("radius") Integer radius,
                                                   @Param("startTime") Timestamp startTime,
                                                   @Param("endTime") Timestamp endTime);


    /**
     * -- 子查询 1：从 parking_periods 表中筛选出包含 12 - 14 点的 parking_spots_id
     * WITH available_periods AS (
     *     SELECT
     *         parking_spots_id
     *     FROM
     *         parking_periods
     *     WHERE
     *         start_time <= '12:00:00'
     *         AND end_time >= '14:00:00'
     *         AND deleted_at = 0  -- 排除已删除的记录
     * ),
     * -- 子查询 2：从 parking_occupied 表中筛选出包含 12 - 14 点的 parking_spots_id
     * occupied_periods AS (
     *     SELECT
     *         DISTINCT parking_spots_id
     *     FROM
     *         parking_occupied
     *     WHERE
     *         (start_time < '14:00:00' AND end_time > '12:00:00')
     *         AND deleted_at = 0  -- 排除已删除的记录
     * )
     * -- 主查询：从 available_periods 中排除 occupied_periods 中的 parking_spots_id
     * SELECT
     *     ap.parking_spots_id
     * FROM
     *     available_periods ap
     * WHERE
     *     ap.parking_spots_id NOT IN (SELECT parking_spots_id FROM occupied_periods);
     */
    @Select("SELECT ps.* " +
            "FROM parking_spots ps " +
            "WHERE ps.id NOT IN ( " +
            "    SELECT pp.parking_spots_id " +
            "    FROM parking_periods pp " +
            "    WHERE pp.status = 3 " +
            "    AND #{startTime} >= pp.start_time AND #{endTime} <= pp.end_time " +
            "    AND pp.deleted_at = 0 " +
            "    AND pp.id NOT IN ( " +
            "        SELECT po.parking_periods_id " +
            "        FROM parking_occupied po " +
            "        WHERE po.deleted_at = 0 " +
            "        AND (po.start_time < #{endTime} AND po.end_time > #{startTime}) " +
            "    ) " +
            ") " +
            "AND ST_Distance_Sphere( " +
            "    POINT(#{longitude}, #{latitude}), " +
            "    POINT(ps.longitude, ps.latitude) " +
            ") <= #{radius} " +
            "GROUP BY ps.id " +
            "ORDER BY ST_Distance_Sphere( " +
            "    POINT(#{longitude}, #{latitude}), " +
            "    POINT(ps.longitude, ps.latitude) " +
            ")")
    IPage<ParkingSpot> selectAvailableParkingSpots2(Page<ParkingSpot> page,
                                                   @Param("latitude") BigDecimal latitude,
                                                   @Param("longitude") BigDecimal longitude,
                                                   @Param("radius") Integer radius,
                                                   @Param("startTime") Timestamp startTime,
                                                   @Param("endTime") Timestamp endTime);

}