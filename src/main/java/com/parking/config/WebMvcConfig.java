package com.parking.config;

import com.parking.interceptor.JwtTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
                        "/api/user/login/**",   // 登录接口
                        "/api/user/register",   // 注册接口
                        "/api/user/sendVerifyCode",   // 发送验证码
                        "/api/user/verifyCode",   // 验证码验证
                        "/api/pay/notify/**",   // 支付回调接口
                        "/api/upload/**"    // 文件上传接口
                );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:/uploads/");
//    }
}