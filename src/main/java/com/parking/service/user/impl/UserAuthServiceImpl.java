package com.parking.service.user.impl;

import com.parking.enums.user.SourceFromEnum;
import com.parking.enums.user.UserRoleEnum;
import com.parking.enums.user.UserStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.UserLoginRequest;
import com.parking.model.param.user.response.UserLoginResponse;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.sms.SmsService;
import com.parking.service.user.UserAuthService;
import com.parking.util.tool.AesUtil;
import com.parking.util.tool.JwtUtil;
import com.parking.util.WechatIdUtil;
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
    public UserLoginResponse wechatLogin(String code) {
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
    public UserLoginResponse phoneLogin(UserLoginRequest request) {

        validateLoginRequest(request);

        // 1. 验证手机号是否已注册
        User user = userRepository.findByPhone(request.getPhone());
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
    public OperationResponse register(UserLoginRequest request) {

        validateLoginRequest(request);

        // 1. 验证手机号是否已注册
        User usr = userRepository.findByPhone(request.getPhone());
        if (usr != null) {
            throw new BusinessException("400", "phone is already in use");
        }

        // 2. 验证验证码
        boolean verifyCode = smsService.verifyCode(request.getPhone(), request.getVerifyCode());
        if (!verifyCode) {
            throw new BusinessException("400", "invalid verify code");
        }

        // 3. 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 默认普通用户
        user.setRole(UserRoleEnum.USER.getRole());
        // 默认激活状态
        user.setStatus(UserStatusEnum.ACTIVE.getStatus());
        // 默认来源
        user.setSourceFrom(SourceFromEnum.APP.getSourceFrom());
        userRepository.insert(user);

        return OperationResponse.operationSuccess(user.getId(), "register success");
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
    private UserLoginResponse generateLoginResponse(User user) {
        // 生成token
        String token = jwtUtil.generateToken(user.getId());

        // 构建响应
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(aesUtil.decrypt(user.getPhone()));
        response.setNickName(user.getNickName());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
} 