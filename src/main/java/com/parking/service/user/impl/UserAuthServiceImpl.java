package com.parking.service.user.impl;

import com.parking.model.dto.user.response.UserLoginResponse;
import com.parking.model.entity.User;
import com.parking.repository.UserRepository;
import com.parking.service.user.UserAuthService;
import com.parking.util.JwtUtil;
import com.parking.util.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WxUtil wxUtil;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserLoginResponse wxLogin(String code) {
        // 1. 调用微信接口获取openid
        String openId = wxUtil.getOpenId(code);
        
        // 2. 查找或创建用户
        User user = userRepository.findByOpenId(openId)
                .orElseGet(() -> createUser(openId));
        
        // 3. 生成token
        String token = jwtUtil.generateToken(user.getId());
        
        // 4. 构建响应
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUserId(user.getId().toString());
        
        UserLoginResponse.UserInfo userInfo = new UserLoginResponse.UserInfo();
        userInfo.setNickName(user.getNickName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    private User createUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        user.setCreateTime(LocalDateTime.now());
        return userRepository.save(user);
    }
} 