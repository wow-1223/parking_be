package com.parking.service2.user;

import com.parking.model.dto.user.UserLoginDTO;
import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.user.request.UserLoginRequest;

public interface LoginService {

    /**
     * 微信登录
     */
    LoginResponse<UserLoginDTO> wechatLogin(String code);

    /**
     * 手机登录
     */
    LoginResponse<UserLoginDTO> phoneLogin(UserLoginRequest request);

    /**
     * 注册
     */
    LoginResponse<Long> register(UserLoginRequest request);
}
