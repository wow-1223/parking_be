package com.parking.repository.jpa;

import com.parking.model.entity.jpa.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 获取用户的收藏列表
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * 检查是否已收藏
     */
    boolean existsByUserIdAndParkingSpotId(Long userId, Long parkingSpotId);

    /**
     * 删除收藏
     */
    void deleteByUserIdAndParkingSpotId(Long userId, Long parkingSpotId);

    /**
     * 统计停车位的收藏数
     */
    long countByParkingSpotId(Long parkingSpotId);

    /**
     * 统计用户的收藏数
     */
    long countByUserId(Long userId);

    /**
     * 根据用户ID和停车位ID查找收藏
     */
    Optional<Favorite> findByUserIdAndParkingSpotId(Long userId, Long parkingSpotId);
} 