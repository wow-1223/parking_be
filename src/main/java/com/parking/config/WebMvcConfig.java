package com.parking.config;

import com.parking.interceptor.JwtTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/api/**")  // 需要验证token的路径
                .excludePathPatterns(        // 不需要验证token的路径
                        "/api/user/login",   // 登录接口
                        "/api/upload/**",    // 文件上传接口
                        "/swagger-ui/**",    // Swagger文档
                        "/v3/api-docs/**"    // OpenAPI文档
                );
    }
}