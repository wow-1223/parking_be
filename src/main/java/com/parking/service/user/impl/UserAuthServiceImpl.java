package com.parking.service.user.impl;

import com.parking.enums.user.SourceFromEnum;
import com.parking.enums.user.UserRoleEnum;
import com.parking.enums.user.UserStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.model.param.common.LoginResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.model.dto.user.UserLoginDTO;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.UserRepository;
import com.parking.handler.sms.SmsService;
import com.parking.service.user.UserAuthService;
import com.parking.handler.encrypt.AesUtil;
import com.parking.handler.jwt.JwtUtil;
import com.parking.handler.WechatIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WechatIdUtil wechatIdUtil;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AesUtil aesUtil;

    @Override
    public LoginResponse<UserLoginDTO> wechatLogin(String code) {
        // 1. 调用微信接口获取openid
        String openId = wechatIdUtil.getOpenId(code);
        
        // 2. 查找或创建用户
        User user = userRepository.findByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setSourceFrom(SourceFromEnum.WECHAT.getSourceFrom());
            userRepository.insert(user);
            user.setId(user.getId());
        }

        return generateLoginResponse(user);
    }

    @Override
    public LoginResponse<UserLoginDTO> phoneLogin(UserLoginRequest request) {

        validateLoginRequest(request);

        // 1. 验证手机号是否已注册
        User user = userRepository.findByPhone(aesUtil.encrypt(request.getPhone()));
        if (user == null) {
            throw new BusinessException("phone is not registered");
        }

        if (StringUtils.isNotBlank(request.getVerifyCode())) {
            // 2. 验证验证码
            boolean verifyCode = smsService.verifyCode(request.getPhone(), request.getVerifyCode());
            if (!verifyCode) {
                throw new BusinessException("verify code error");
            }
        } else {
            // 2. 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException("password error");
            }
        }

        // 3. 验证用户状态
        if (user.getStatus() != UserStatusEnum.ACTIVE.getStatus()) {
            throw new BusinessException("user is not active");
        }

        return generateLoginResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse<Long> register(UserLoginRequest request) {

        validateLoginRequest(request);

        // 1. 验证手机号是否已注册
        User usr = userRepository.findByPhone(aesUtil.encrypt(request.getPhone()));
        if (usr != null) {
            throw new BusinessException("phone is already in use");
        }

        // 2. 验证验证码
        boolean verifyCode = smsService.verifyCode(request.getPhone(), request.getVerifyCode());
        if (!verifyCode) {
            throw new BusinessException("invalid verify code");
        }

        // 3. 创建用户
        User user = new User();
        user.setPhone(aesUtil.encrypt(request.getPhone()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 默认普通用户
        user.setRole(UserRoleEnum.USER.getRole());
        // 默认激活状态
        user.setStatus(UserStatusEnum.ACTIVE.getStatus());
        // 默认来源
        user.setSourceFrom(SourceFromEnum.APP.getSourceFrom());
        userRepository.insert(user);

        return LoginResponse.loginSuccess(user.getId(), "register success");
    }

    /**
     * 验证登录请求参数
     * @param request request
     */
    private void validateLoginRequest(UserLoginRequest request) {
        // 验证手机号格式
        if (StringUtils.isBlank(request.getPhone())) {
            throw new BusinessException("phone is required");
        }

        if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("invalid phone number");
        }

        if (StringUtils.isBlank(request.getPassword()) && StringUtils.isBlank(request.getVerifyCode())) {
            throw new BusinessException("password or verify code is required");
        }

        // 验证验证码格式
        if (request.getVerifyCode() != null && !request.getVerifyCode().matches("\\d{6}")) {
            throw new BusinessException("invalid verify code");
        }

        // 验证密码格式
        if (request.getPassword()!= null &&!request.getPassword().matches("^[a-zA-Z0-9]{6,20}$")) {
            throw new BusinessException("invalid password");
        }
    }

    /**
     * 生成登录响应
     * @param user user
     * @return response
     */
    private LoginResponse<UserLoginDTO> generateLoginResponse(User user) {
        // 生成token
        String token = jwtUtil.generateToken(user.getId());

        // 构建响应
        UserLoginDTO response = new UserLoginDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(aesUtil.decrypt(user.getPhone()));
        response.setNickName(user.getNickName());
        response.setAvatarUrl(user.getAvatarUrl());
        return LoginResponse.loginSuccess(response, "login success");
    }
} 