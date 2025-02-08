package com.parking.controller.user;

import com.parking.model.dto.user.request.UserLoginRequest;
import com.parking.model.dto.user.response.UserLoginResponse;
import com.parking.service.user.UserAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户认证接口")
@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @ApiOperation("微信登录")
    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        return userAuthService.wxLogin(request.getCode());
    }
} 