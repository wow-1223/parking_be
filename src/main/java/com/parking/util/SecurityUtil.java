package com.parking.util;

import com.parking.common.exception.UnauthorizedException;
import com.parking.model.entity.User;
import com.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
@Component
public class SecurityUtil {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("用户未登录");
        }
        return Long.parseLong(authentication.getName());
    }

    /**
     * 获取当前登录用户信息
     * @return 用户实体
     */
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("用户不存在"));
    }

    /**
     * 检查当前用户是否为管理员
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.isAdmin();
    }

    /**
     * 检查当前用户是否为车位所有者
     * @return 是否为车位所有者
     */
    public boolean isOwner() {
        User user = getCurrentUser();
        return user.isOwner();
    }

    /**
     * 检查是否有权限访问指定用户的资源
     * @param userId 目标用户ID
     * @return 是否有权限
     */
    public boolean hasUserPermission(Long userId) {
        User currentUser = getCurrentUser();
        // 管理员可以访问所有用户的资源
        if (isAdmin()) {
            return true;
        }
        // 普通用户只能访问自己的资源
        return currentUser.getId().equals(userId);
    }

    /**
     * 检查是否有权限访问指定车位的资源
     * @param ownerId 车位所有者ID
     * @return 是否有权限
     */
    public boolean hasParkingPermission(Long ownerId) {
        User currentUser = getCurrentUser();
        // 管理员可以访问所有车位
        if (isAdmin()) {
            return true;
        }
        // 车位所有者只能访问自己的车位
        return currentUser.getId().equals(ownerId);
    }

    /**
     * 检查用户角色是否匹配
     * @param role 需要检查的角色
     * @return 是否匹配
     */
    public boolean hasRole(String role) {
        User user = getCurrentUser();
        return role.equals(user.getRole());
    }

    /**
     * 检查用户是否处于活跃状态
     * @return 是否活跃
     */
    public boolean isActive() {
        User user = getCurrentUser();
        return "ACTIVE".equals(user.getStatus());
    }
} 