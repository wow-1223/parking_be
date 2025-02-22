package com.parking.handler.sms;

public interface SmsService {

    /**
     * 发送验证码
     */
//    void sendVerifyCode(String phone);
    // todo 测试：返回验证码
    String sendVerifyCode(String phone);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);
}