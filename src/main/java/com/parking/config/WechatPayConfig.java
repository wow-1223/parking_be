package com.parking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {
    /**
     * 微信商户号
     */
    private String mchId;

    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;

    /**
     * 商户私钥文件路径
     */
    private String privateKeyPath;

    /**
     * API V3密钥
     */
    private String apiV3Key;

    /**
     * APPID
     */
    private String appId;

    /**
     * 微信支付通知回调地址
     */
    private String notifyUrl;
}