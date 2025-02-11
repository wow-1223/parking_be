package com.parking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {

    /**
     * 上传路径
     */
    private String uploadPath;

    /**
     * 允许的文件类型
     */
    private String[] allowTypes;

    /**
     * 最大文件大小(MB)
     */
    private Integer maxSize;

    /**
     * 是否使用云存储
     */
    private Boolean useCloud;

    /**
     * 云存储配置
     */
    private CloudStorage cloudStorage = new CloudStorage();

    @Data
    public static class CloudStorage {
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
        private String region;
        private String endpoint;
        private String basePath;
        private String customDomain;
        private Boolean useHttps = true;
        private Boolean privateBucket = false;
        private Integer urlExpire = 3600;
    }
}