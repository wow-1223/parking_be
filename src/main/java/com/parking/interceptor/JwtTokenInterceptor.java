package com.parking.interceptor;

import com.parking.exception.UnauthorizedException;
import com.parking.util.tool.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT Token拦截器
 */
@Slf4j
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String token = extractToken(request);
            if (StringUtils.hasText(token)) {
                // 验证token
                if (jwtUtil.validateToken(token)) {
                    // 获取用户ID
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    // 设置认证信息到Security上下文
                    setAuthentication(userId);
                    return true;
                }
            }
            throw new UnauthorizedException("invalid token");
        } catch (Exception e) {
            log.error("Token verify failed", e);
            throw new UnauthorizedException("Token verify failed", e);
        }
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 设置认证信息
     */
    private void setAuthentication(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId.toString(), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}