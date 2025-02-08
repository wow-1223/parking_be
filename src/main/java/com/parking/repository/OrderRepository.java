package com.parking.repository;

import com.parking.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

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
    List<Object[]> getEarningsStatistics(Long ownerId, LocalDateTime startDate, 
            LocalDateTime endDate, String timeRange);

    @Query(value = """
            SELECT DATE(o.start_time) as date,
                   COUNT(*) * 100.0 / 
                   (SELECT COUNT(*) FROM orders 
                    WHERE parking_spot_id = :parkingId 
                    AND start_time BETWEEN :startDate AND :endDate) as usage_rate
            FROM orders o
            WHERE o.parking_spot_id = :parkingId
            AND o.start_time BETWEEN :startDate AND :endDate
            GROUP BY DATE(o.start_time)
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> getParkingUsageStatistics(Long parkingId, LocalDateTime startDate,
            LocalDateTime endDate, String timeRange);
} 