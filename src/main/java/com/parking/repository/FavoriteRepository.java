package com.parking.repository;

import com.parking.model.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Optional<Favorite> findByUserIdAndParkingSpotId(Long userId, Long parkingSpotId);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    boolean existsByUserIdAndParkingSpotId(Long userId, Long parkingSpotId);
} 