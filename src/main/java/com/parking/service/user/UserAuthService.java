package com.parking.service.user;

import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.model.dto.user.UserLoginDTO;

public interface UserAuthService {

    /**
     * 微信登录
     * @param code 微信登录code
     * @return 登录响应信息
     */
    LoginResponse<UserLoginDTO> wechatLogin(String code);

    /**
     * 手机登录
     * @param request 登录请求信息
     * @return 登录响应信息
     */
    LoginResponse<UserLoginDTO> phoneLogin(UserLoginRequest request);

    /**
     * 注册
     * @param request 注册请求信息
     * @return 注册响应信息
     */
    LoginResponse<Long> register(UserLoginRequest request);
} 