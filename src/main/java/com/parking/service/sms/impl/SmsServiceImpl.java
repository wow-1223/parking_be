package com.parking.service.sms.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import com.parking.config.SmsConfig;
import com.parking.exception.BusinessException;
import com.parking.service.sms.SmsService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private static final String VERIFY_CODE_PREFIX = "sms:verify:";
    private static final String SEND_LIMIT_PREFIX = "sms:limit:";
    private static final long VERIFY_CODE_EXPIRE = 5; // 验证码有效期5分钟
    private static final long SEND_LIMIT_EXPIRE = 60; // 发送限制1分钟
    private static final Gson gson = new Gson();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Client smsClient;

    @Autowired
    private SmsConfig smsConfig;

    @Override
    public String sendVerifyCode(String phone) {
        // 1. 检查发送频率限制
        String limitKey = SEND_LIMIT_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new BusinessException("send verify code too frequently, please try again later");
        }

        try {
            // 2. 生成6位随机验证码
            String code = String.format("%06d", new Random().nextInt(1000000));

            // 3. 构建短信参数
            Map<String, String> templateParam = new HashMap<>();
            templateParam.put("code", code);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsConfig.getSignName())
                    .setTemplateCode(smsConfig.getTemplateCode())
                    .setTemplateParam(gson.toJson(templateParam));

            RuntimeOptions runtime = new RuntimeOptions();

            // 4. 发送短信
            // todo: 测试时注释掉
//            SendSmsResponse response = smsClient.sendSmsWithOptions(sendSmsRequest, runtime);
//            log.info("send sms message failed: {}", gson.toJson(response));

//            if (!"OK".equals(response.getBody().getCode())) {
//                throw new BusinessException("send sms message failed: " + response.getBody().getMessage());
//            }

            // 5. 将验证码保存到Redis
            String verifyKey = VERIFY_CODE_PREFIX + phone;
            redisTemplate.opsForValue().set(verifyKey, code, VERIFY_CODE_EXPIRE, TimeUnit.MINUTES);

            // 6. 设置发送限制
            redisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_EXPIRE, TimeUnit.SECONDS);
            return code;
        } catch (Exception e) {
            log.error("send sms message error", e);
            throw new BusinessException("", "send sms message failed, please try again later");
        }
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String key = VERIFY_CODE_PREFIX + phone;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode != null && savedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}