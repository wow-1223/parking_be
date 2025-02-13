package com.parking.repository.jpa;

import com.parking.model.entity.jpa.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓库接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据openId查找用户
     */
    @Query("SELECT u FROM User u WHERE u.openId = :openId")
    Optional<User> findByOpenId(String openId);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 查询所有活跃用户
     * @return 活跃用户列表
     */
    @Query("SELECT u FROM User u WHERE u.status = 1")
    List<User> findAllActiveUsers();

    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查openId是否已存在
     */
    boolean existsByOpenId(String openId);

    /**
     * 查找指定角色的用户
     */
    Page<User> findByRole(String role, Pageable pageable);

    /**
     * 查找指定状态的用户
     */
    Page<User> findByStatus(String status, Pageable pageable);

    /**
     * 搜索用户
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.nickName LIKE %:keyword% OR " +
            "u.phone LIKE %:keyword%")
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找活跃的车位所有者
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.role = 'OWNER' AND u.status = 'ACTIVE'")
    List<User> findActiveOwners();

    /**
     * 统计指定角色的用户数量
     */
    long countByRole(String role);
}