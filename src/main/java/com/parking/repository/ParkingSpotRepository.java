package com.parking.repository;

import com.parking.model.entity.ParkingSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    
    @Query(value = "SELECT *, " +
            "ST_Distance_Sphere(point(longitude, latitude), point(?1, ?2)) as distance " +
            "FROM parking_spots " +
            "WHERE ST_Distance_Sphere(point(longitude, latitude), point(?1, ?2)) <= ?3 " +
            "AND status = 'available' " +
            "ORDER BY distance", 
            nativeQuery = true)
    Page<ParkingSpot> findNearbyParkings(Double longitude, Double latitude, 
            Integer radius, Pageable pageable);
    
    @Query("SELECT p FROM ParkingSpot p WHERE p.location LIKE %?1% " +
            "OR p.description LIKE %?1%")
    Page<ParkingSpot> searchParkings(String keyword, Pageable pageable);

    @Query("SELECT p FROM ParkingSpot p WHERE p.owner.id = :ownerId")
    Page<ParkingSpot> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT p FROM ParkingSpot p WHERE p.owner.id = :ownerId AND p.status = :status")
    Page<ParkingSpot> findByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);
} 