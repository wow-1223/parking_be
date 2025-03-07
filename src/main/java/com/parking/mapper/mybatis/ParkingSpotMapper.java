package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.model.entity.mybatis.ParkingSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ParkingSpotMapper extends BaseMapper<ParkingSpot> {

    String GET_AVAILABLE_SPOTS_SQL = """
            SELECT 
                id, 
                rules, 
                price,
                parking_type,
                ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{longitude}, #{latitude})) as distance
            FROM parking_spots
            WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{longitude}, #{latitude})) <= #{radius}
                AND (#{maxPrice} IS NULL OR price <= #{maxPrice})
                AND (#{minPrice} IS NULL OR price >= #{minPrice})
                AND (#{parkingType} IS NULL OR parking_type = #{parkingType})
                AND status = 2
                AND deleted_at = 0
            """;

    String GET_FAVORITE_SPOTS_SQL = """
            SELECT 
                ps.id,
                ps.owner_id,
                ps.location,
                ps.longitude,
                ps.latitude,
                ps.price
            FROM parking_spots ps
            LEFT JOIN favorites f ON ps.id = f.parking_spot_id
            WHERE f.user_id = #{userId}
                AND ps.deleted_at = 0
                AND f.deleted_at = 0
            """;

    /**
     * 查找附近可用的停车位
     *
     * @param longitude   经度
     * @param latitude    纬度
     * @param radius      半径
     * @param maxPrice    最大价格
     * @param minPrice    最小价格
     * @param parkingType 停车类型
     * @return 附近可用的停车位列表
     */
    @Select(GET_AVAILABLE_SPOTS_SQL)
    List<ParkingSpot> getAvailableParkingSpotIdList(@Param("longitude") Double longitude,
                                                    @Param("latitude") Double latitude,
                                                    @Param("radius") Integer radius,
                                                    @Param("maxPrice") BigDecimal maxPrice,
                                                    @Param("minPrice") BigDecimal minPrice,
                                                    @Param("parkingType") Integer parkingType);

    /**
     * 获取用户收藏的停车位列表
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页的停车位列表
     */
    @Select(GET_FAVORITE_SPOTS_SQL)
    IPage<ParkingSpot> getFavoriteParkingSpots(Page<ParkingSpot> page, @Param("userId") Long userId);
}