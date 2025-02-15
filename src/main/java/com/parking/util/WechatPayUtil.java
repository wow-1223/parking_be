package com.parking.util;

import com.parking.config.WechatPayConfig;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.*;

@Slf4j
@Component
public class WechatPayUtil {

    @Autowired
    private WechatPayConfig wechatPayConfig;

    private static final int TAG_LENGTH_BIT = 128;

    /**
     * 构建JSAPI支付参数
     */
    public Map<String, String> buildJsapiPayParams(String prepayId) {
        try {
            String nonceStr = generateNonceStr();
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            // 构建签名字符串
            String signStr = wechatPayConfig.getAppId() + "\n" +
                    timestamp + "\n" +
                    nonceStr + "\n" +
                    "prepay_id=" + prepayId + "\n";

            // 加载商户私钥
            PrivateKey privateKey = PemUtil.loadPrivateKey(
                    new FileInputStream(wechatPayConfig.getPrivateKeyPath()));

            // 计算签名值
            String signature = sign(signStr.getBytes(StandardCharsets.UTF_8), privateKey);

            // 构建支付参数
            Map<String, String> params = new HashMap<>();
            params.put("appId", wechatPayConfig.getAppId());
            params.put("timeStamp", timestamp);
            params.put("nonceStr", nonceStr);
            params.put("package", "prepay_id=" + prepayId);
            params.put("signType", "RSA");
            params.put("paySign", signature);

            return params;

        } catch (Exception e) {
            log.error("build JSAPI pay params failed:", e);
            throw new RuntimeException("build JSAPI pay params failed:", e);
        }
    }

    /**
     * 验证回调签名
     */
    public boolean verifyNotifySign(String notifyData, String signature,
                                    String nonce, String timestamp, String serialNumber) {
        try {
            // 获取验证器
            Verifier verifier = CertificatesManager
                    .getInstance()
                    .getVerifier(wechatPayConfig.getMchId());

            // 构建验签名串
            String message = buildMessage(notifyData, timestamp, nonce);

            // 对验签名串和签名进行验证
            return verifier.verify(serialNumber, message.getBytes(StandardCharsets.UTF_8),
                    Arrays.toString(Base64.getDecoder().decode(signature)));

        } catch (Exception e) {
            log.error("verify notify sign failed:", e);
            return false;
        }
    }

    /**
     * 解密回调数据
     */
    public String decryptNotifyData(String associatedData, String nonce, String ciphertext) {
        try {
            AesUtil aesUtil = new AesUtil(wechatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
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
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 签名
     */
    private String sign(byte[] message, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] signatureBytes = cipher.doFinal(sha256(message));
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * SHA256哈希
     */
    private byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    /**
     * 构建验签名串
     */
    private String buildMessage(String body, String timestamp, String nonce) {
        return timestamp + "\n" +
                nonce + "\n" +
                body + "\n";
    }
}