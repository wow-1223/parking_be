package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class UserLoginRequest {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 密码
     */
    private String password;
}