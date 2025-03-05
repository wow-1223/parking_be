package com.parking.controller.user;

import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.model.dto.user.UserLoginDTO;
import com.parking.handler.sms.SmsService;
import com.parking.service.user.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登陆注册相关接口
 */
@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private SmsService smsService;

    /**
     * 测试接口，返回字符串<br>
     * @return 测试接口，返回字符串
     */
    @GetMapping("/test")
    public String test() {
        return "test succeed";
    }

    /**
     * 微信登录接口
     * @param code 微信code，用于获取openid和session_key，用于登录
     * @return 登录成功返回用户信息，失败返回错误码和错误信息
     */
    @PostMapping("/login/wechat/{code}")
    public LoginResponse<UserLoginDTO> login(@PathVariable("code") String code) {
        return userAuthService.wechatLogin(code);
    }

    /**
     * 手机号登录接口
     * @param request 手机号和验证码，用于登录
     * @return 登录成功返回用户信息，失败返回错误码和错误信息
     */
    @PostMapping("/login/phone")
    public LoginResponse<UserLoginDTO> login(@RequestBody UserLoginRequest request) {
        return userAuthService.phoneLogin(request);
    }

    /**
     * 发送验证码接口
     * @param phone 手机号
     * @return message
     */
    @PostMapping("/sendVerifyCode")
    public LoginResponse<String> sendVerifyCode(@RequestParam String phone) {
        String code = smsService.sendVerifyCode(phone);
        return LoginResponse.loginSuccess(code, "send verify code success");
    }

    /**
     * 注册接口
     * @param request 手机号和验证码
     * @return 注册成功返回用户id，失败返回错误码和错误信息
     */
    @PostMapping("/register")
    public LoginResponse<Long> register(@Validated @RequestBody UserLoginRequest request) {
        return userAuthService.register(request);
    }

} 