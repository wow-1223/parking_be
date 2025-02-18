package com.parking.model.param.user.response;

import lombok.Data;

@Data
public class UserLoginResponse {

    private String token;
    private Long userId;
    private String phone;
    private String nickName;
    private String avatarUrl;
}