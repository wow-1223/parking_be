package com.parking.repository.jpa;

import com.parking.model.entity.jpa.ParkingSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    /**
     * 查找附近可用的停车位
     */
    @Query(value = "SELECT * FROM parking_spots p " +
            "WHERE p.status = 'available' " +
            "AND ST_Distance_Sphere(" +
            "    point(p.longitude, p.latitude), " +
            "    point(:longitude, :latitude)" +
            ") <= :radius " +
            "ORDER BY ST_Distance_Sphere(" +
            "    point(p.longitude, p.latitude), " +
            "    point(:longitude, :latitude)" +
            ")",
            countQuery = "SELECT count(*) FROM parking_spots p " +
                    "WHERE p.status = 'available' " +
                    "AND ST_Distance_Sphere(" +
                    "    point(p.longitude, p.latitude), " +
                    "    point(:longitude, :latitude)" +
                    ") <= :radius",
            nativeQuery = true)
    Page<ParkingSpot> findNearbyAvailable(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Integer radius,
            Pageable pageable);

    /**
     * 搜索停车位
     */
    @Query("SELECT p FROM ParkingSpot p WHERE " +
            "p.status = 'available' AND " +
            "(p.location LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<ParkingSpot> search(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找车主的停车位
     */
    @Query("SELECT p FROM ParkingSpot p WHERE " +
            "(:ownerId IS NULL OR p.ownerId = :ownerId) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<ParkingSpot> findByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);

    /**
     * 查找指定状态的停车位
     */
    Page<ParkingSpot> findByStatus(String status, Pageable pageable);

    /**
     * 统计车主的停车位数量
     */
    long countByOwnerId(Long ownerId);

    /**
     * 检查停车位是否属于指定车主
     */
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
} 