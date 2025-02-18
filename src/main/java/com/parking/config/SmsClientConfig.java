package com.parking.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsClientConfig {

    @Autowired
    private SmsConfig smsConfig;

    @Bean
    public Client smsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(smsConfig.getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAccessKeySecret())
                .setEndpoint(smsConfig.getEndpoint());
        return new Client(config);
    }
}