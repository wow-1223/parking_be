package com.parking.controller.user;

import com.parking.model.dto.user.request.UserLoginRequest;
import com.parking.model.dto.user.response.UserLoginResponse;
import com.parking.service.user.UserAuthService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/test")
    public String test() {
        return "test succeed";
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        return userAuthService.wxLogin(request.getCode());
    }
} 