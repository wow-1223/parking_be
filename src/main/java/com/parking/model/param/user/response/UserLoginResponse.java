package com.parking.model.param.user.response;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String token;
    private String userId;
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private String nickName;
        private String avatarUrl;
    }
}