package com.parking.util;

import com.parking.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 微信工具类
 */
@Slf4j
@Component
public class WxUtil {

    @Value("${wx.appid}")
    private String appId;

    @Value("${wx.secret}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String WX_AUTH_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 获取微信openId
     */
    public String getOpenId(String code) {
        // todo for test
        if (code != null) {
            return code;
        }

        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_AUTH_URL, appId, secret, code);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, String> body = response.getBody();

            if (body != null && body.containsKey("openid")) {
                return body.get("openid");
            }

            if (body != null && body.containsKey("errmsg")) {
                throw new BusinessException("WX001", body.get("errmsg"));
            }

            throw new BusinessException("WX002", "获取openId失败");
        } catch (Exception e) {
            log.error("调用微信接口失败", e);
            throw new BusinessException("WX003", "微信服务调用失败");
        }
    }

    /**
     * 验证微信支付签名
     */
    public boolean verifySign(Map<String, String> params, String sign) {
        // TODO: 实现微信支付签名验证
        return true;
    }

    /**
     * 发送微信模板消息
     */
    public void sendTemplateMessage(String openId, String templateId, Map<String, Object> data) {
        // TODO: 实现发送微信模板消息
    }
}