package com.parking.controller.user;

import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.model.dto.user.UserLoginDTO;
import com.parking.service.sms.SmsService;
import com.parking.service.user.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private SmsService smsService;

    @GetMapping("/test")
    public String test() {
        return "test succeed";
    }

    @PostMapping("/login/wechat/{code}")
    public LoginResponse<UserLoginDTO> login(@PathVariable("code") String code) {
        return userAuthService.wechatLogin(code);
    }

    @PostMapping("/login/phone")
    public LoginResponse<UserLoginDTO> login(@RequestBody UserLoginRequest request) {
        return userAuthService.phoneLogin(request);
    }

    @PostMapping("/sendVerifyCode")
    public LoginResponse<String> sendVerifyCode(@RequestParam String phone) {
        String code = smsService.sendVerifyCode(phone);
        return LoginResponse.loginSuccess(code, "send verify code success");
    }

    @PostMapping("/register")
    public LoginResponse<Long> register(@Validated @RequestBody UserLoginRequest request) {
        return userAuthService.register(request);
    }

} 