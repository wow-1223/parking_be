package com.parking.repository.jpa;

import com.parking.model.entity.jpa.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 查找订单列表
     * @param userId 用户ID
     * @param parkingSpotId 停车位ID（可选）
     * @param status 订单状态（可选）
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:parkingSpotId IS NULL OR o.parkingSpot.id = :parkingSpotId) AND " +
            "(:status IS NULL OR o.status = :status) " +
            "ORDER BY o.createTime DESC")
    Page<Order> findOrders(
            @Param("userId") Long userId,
            @Param("parkingSpotId") Long parkingSpotId,
            @Param("status") String status,
            Pageable pageable);


    /**
     * 查找车位所有者的订单
     */
    @Query("SELECT o FROM Order o WHERE o.parkingSpot.owner.id = :ownerId")
    Page<Order> findByParkingSpotOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    /**
     * 查找车位所有者的指定状态订单
     */
    @Query("SELECT o FROM Order o WHERE " +
            "o.parkingSpot.owner.id = :ownerId AND " +
            "o.status = :status")
    Page<Order> findByParkingSpotOwnerIdAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 获取整体使用统计
     */
    @Query(value = """
            SELECT DATE(o.start_time) as date,
                   COUNT(*) * 100.0 / 
                   (SELECT COUNT(*) FROM orders o2
                    JOIN parking_spots p2 ON o2.parking_spot_id = p2.id 
                    WHERE p2.owner_id = :ownerId 
                    AND o2.start_time BETWEEN :startDate AND :endDate) as usage_rate
            FROM orders o
            JOIN parking_spots p ON o.parking_spot_id = p.id
            WHERE p.owner_id = :ownerId
            AND o.start_time BETWEEN :startDate AND :endDate
            GROUP BY DATE(o.start_time)
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> getOverallUsageStatistics(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 获取收益统计
     */
    @Query(value = """
            SELECT DATE(o.start_time) as date,
                   SUM(o.amount) as total_amount,
                   COUNT(*) as order_count
            FROM orders o
            JOIN parking_spots p ON o.parking_spot_id = p.id
            WHERE p.owner_id = :ownerId
            AND o.start_time BETWEEN :startDate AND :endDate
            AND o.status = 'completed'
            GROUP BY DATE(o.start_time)
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> getEarningsStatistics(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 获取单个停车位使用率统计
     */
    @Query(value = """
            SELECT DATE(o.start_time) as date,
                   COUNT(*) * 100.0 / 
                   (SELECT COUNT(*) FROM orders 
                    WHERE parking_spot_id = :parkingSpotId 
                    AND start_time BETWEEN :startDate AND :endDate) as usage_rate
            FROM orders o
            WHERE o.parking_spot_id = :parkingSpotId
            AND o.start_time BETWEEN :startDate AND :endDate
            GROUP BY DATE(o.start_time)
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> getParkingUsageStatistics(
            @Param("parkingSpotId") Long parkingSpotId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
} 