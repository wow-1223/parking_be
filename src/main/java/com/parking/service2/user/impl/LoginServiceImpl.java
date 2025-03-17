package com.parking.service2.user.impl;

import com.parking.model.dto.user.UserLoginDTO;
import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.service2.user.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public LoginResponse<UserLoginDTO> wechatLogin(String code) {
        return null;
    }

    @Override
    public LoginResponse<UserLoginDTO> phoneLogin(UserLoginRequest request) {
        return null;
    }

    @Override
    public LoginResponse<Long> register(UserLoginRequest request) {
        return null;
    }

}
