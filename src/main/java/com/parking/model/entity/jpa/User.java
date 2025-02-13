package com.parking.model.entity.jpa;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_id", unique = true, nullable = false)
    private String openId;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "role", nullable = false)
    private Byte role;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Column(name = "create_time", nullable = false, insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "deleted_at", nullable = false)
    private Long deletedAt;

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * 检查是否为车位所有者
     */
    public boolean isOwner() {
        return "OWNER".equals(role);
    }

    /**
     * 检查是否处于活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
} 