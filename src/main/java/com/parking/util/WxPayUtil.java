package com.parking.util;

import com.parking.config.WxPayConfig;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.util.Base64;
import java.util.Random;
import java.security.cert.X509Certificate;

@Slf4j
@Component
public class WxPayUtil {

    @Autowired
    private WxPayConfig wxPayConfig;

    /**
     * 生成随机字符串
     */
    public String generateNonceStr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成时间戳
     */
    public String generateTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 签名生成
     */
    public String generateSignature(String prepayId, String nonceStr, String timestamp) {
        try {
            String message = wxPayConfig.getAppId() + "\n"
                    + timestamp + "\n"
                    + nonceStr + "\n"
                    + prepayId + "\n";

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

            // TODO: 使用商户私钥对hash进行签名
            return "";
        } catch (Exception e) {
            log.error("生成签名失败", e);
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 解密回调数据
     */
    public String decryptNotifyData(String associatedData, String nonce, String ciphertext) {
        try {
            AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
            return aesUtil.decryptToString(
                    associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    ciphertext
            );
        } catch (Exception e) {
            log.error("解密回调数据失败", e);
            throw new RuntimeException("解密回调数据失败", e);
        }
    }

    /**
     * 验证微信支付回调签名
     */
    public boolean verifyNotifySign(String serialNumber, String body, String signature, String nonce, String timestamp) {
        try {
            // 构建验签名串
            String message = timestamp + "\n"
                    + nonce + "\n"
                    + body + "\n";

            // 获取微信平台证书
            X509Certificate certificate = getCertificate(serialNumber);

            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(certificate.getPublicKey());
            sign.update(message.getBytes(StandardCharsets.UTF_8));

            return sign.verify(Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            log.error("验证签名失败", e);
            return false;
        }
    }

    /**
     * 获取微信平台证书
     */
    private X509Certificate getCertificate(String serialNumber) {
        // TODO: 实现证书管理，可以使用缓存存储证书
        return null;
    }
}