package com.parking.service.user;

import com.parking.model.param.user.response.UserLoginResponse;

public interface UserAuthService {
    /**
     * 微信登录
     * @param code 微信登录code
     * @return 登录响应信息
     */
    UserLoginResponse wechatLogin(String code);
} 