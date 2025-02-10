package com.parking.model.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    /**
     * 微信openid
     */
    @Column(name = "open_id", length = 64, unique = true)
    private String openId;

    /**
     * 昵称
     */
    @Column(name = "nick_name", length = 64)
    private String nickName;

    /**
     * 头像
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 手机号
     */
    @Column(length = 20)
    private String phone;

    /**
     * 角色: USER/OWNER/ADMIN
     */
    @Column(length = 20, nullable = false)
    private String role = "USER";

    /**
     * 状态: ACTIVE/DISABLED
     */
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

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