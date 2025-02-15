package com.parking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String publicKey;

    /**
     * 服务网关地址
     */
    private String serverUrl;

    /**
     * 支付结果通知地址
     */
    private String notifyUrl;

    /**
     * 支付完成后的跳转地址
     */
    private String returnUrl;

    /**
     * 签名方式
     */
    private String signType = "RSA2";

    /**
     * 字符编码
     */
    private String charset = "UTF-8";

    /**
     * 格式
     */
    private String format = "json";
}