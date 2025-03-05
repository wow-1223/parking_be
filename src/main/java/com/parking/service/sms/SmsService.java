package com.parking.service.sms;

public interface SmsService {

    /**
     * 发送验证码
     */
//    void sendVerifyCode(String phone);
    // todo 测试：返回验证码
    String sendVerifyCode(String phone);

    /**
     * 发送订单确认短信
     */
    void sendOrderConfirmMessage(String phone, String message);

    void sendMessage(String phone, String message);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);
}