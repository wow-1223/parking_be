package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String phone;

    private String verifyCode;

    private String password;
}