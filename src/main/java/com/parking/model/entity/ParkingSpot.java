package com.parking.model.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 停车位实体类
 */
@Data
@Entity
@Table(name = "parking_spots")
@DynamicUpdate
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 车位所有者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * 位置描述
     */
    @Column(name = "location")
    private String location;

    /**
     * 纬度
     */
    @Column(nullable = false)
    private Double latitude;

    /**
     * 经度
     */
    @Column(nullable = false)
    private Double longitude;

    /**
     * 详细描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 每小时价格
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * 图片列表
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> images;

    /**
     * 使用规则
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> rules;

    /**
     * 设施列表
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> facilities;

    /**
     * 状态: pending/approved/rejected/available/occupied
     */
    @Column(length = 20, nullable = false)
    private String status = "pending";

    /**
     * 当前订单
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_order_id")
    private Order currentOrder;

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
     * 检查是否可用
     */
    public boolean isAvailable() {
        return "available".equals(status);
    }

    /**
     * 检查是否被占用
     */
    public boolean isOccupied() {
        return "occupied".equals(status);
    }

    /**
     * 检查是否待审核
     */
    public boolean isPending() {
        return "pending".equals(status);
    }

    /**
     * 检查是否已审核通过
     */
    public boolean isApproved() {
        return "approved".equals(status);
    }

    /**
     * 检查是否被拒绝
     */
    public boolean isRejected() {
        return "rejected".equals(status);
    }
} 