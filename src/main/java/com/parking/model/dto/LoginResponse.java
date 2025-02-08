package com.parking.model.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userId;
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private String nickName;
        private String avatarUrl;
    }
} 