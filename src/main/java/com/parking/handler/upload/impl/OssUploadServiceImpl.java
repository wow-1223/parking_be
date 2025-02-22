package com.parking.handler.upload.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.parking.config.UploadConfig;
import com.parking.exception.UploadException;
import com.parking.handler.upload.UploadService;
import com.parking.util.FileUtil;
import com.parking.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "upload.use-cloud", havingValue = "true")
public class OssUploadServiceImpl implements UploadService {

    @Autowired
    private UploadConfig uploadConfig;

    @Override
    public String uploadFile(MultipartFile file) {
        OSS ossClient = null;
        try {
            // 校验文件
            validateFile(file);

            // 压缩图片
            byte[] imageData = ImageUtil.compress(file.getBytes(),
                    file.getOriginalFilename(), 1024);

            // 生成文件名
            String fileName = generateFileName(file.getOriginalFilename());

            // 创建OSS客户端
            ossClient = createOssClient();

            // 上传文件
            ossClient.putObject(uploadConfig.getCloudStorage().getBucket(),
                    fileName, new ByteArrayInputStream(imageData));

            // 返回访问URL
            return getAccessUrl(fileName);

        } catch (Exception e) {
            log.error("upload file to OSS failed", e);
            throw new UploadException("upload file to OSS failed: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public List<String> uploadFiles(MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadFile(file));
        }
        return urls;
    }

    @Override
    public void deleteFile(String fileUrl) {
        OSS ossClient = null;
        try {
            String fileName = getFileNameFromUrl(fileUrl);

            ossClient = createOssClient();
            ossClient.deleteObject(uploadConfig.getCloudStorage().getBucket(), fileName);

        } catch (Exception e) {
            log.error("delete file from OSS failed", e);
            throw new UploadException("delete file from OSS failed: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 创建OSS客户端
     */
    private OSS createOssClient() {
        String endpoint = uploadConfig.getCloudStorage().getEndpoint();
        if (uploadConfig.getCloudStorage().getUseHttps()) {
            endpoint = "https://" + endpoint;
        } else {
            endpoint = "http://" + endpoint;
        }

        return new OSSClientBuilder().build(
                endpoint,
                uploadConfig.getCloudStorage().getAccessKey(),
                uploadConfig.getCloudStorage().getSecretKey());
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = FileUtil.getExtension(originalFilename);
        String basePath = uploadConfig.getCloudStorage().getBasePath();
        String datePath = FileUtil.getDatePath().replace("/", "");

        return (basePath == null ? "" : basePath + "/")
                + datePath + "/"
                + UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 获取访问URL
     */
    private String getAccessUrl(String fileName) {
        // 如果是私有空间，生成带签名的URL
        if (uploadConfig.getCloudStorage().getPrivateBucket()) {
            Date expiration = new Date(System.currentTimeMillis() +
                    uploadConfig.getCloudStorage().getUrlExpire() * 1000);

            OSS ossClient = createOssClient();
            try {
                return ossClient.generatePresignedUrl(
                        uploadConfig.getCloudStorage().getBucket(),
                        fileName,
                        expiration
                ).toString();
            } finally {
                ossClient.shutdown();
            }
        }

        // 使用自定义域名
        String domain = uploadConfig.getCloudStorage().getCustomDomain();
        if (domain == null) {
            domain = uploadConfig.getCloudStorage().getBucket()
                    + "." + uploadConfig.getCloudStorage().getDomain();
        }

        String protocol = uploadConfig.getCloudStorage().getUseHttps() ? "https://" : "http://";
        return protocol + domain + "/" + fileName;
    }

    /**
     * 从URL中获取文件名
     */
    private String getFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        // 校验文件大小
        long size = file.getSize();
        if (size > uploadConfig.getMaxSize() * 1024 * 1024) {
            throw new UploadException("file size can't be over" + uploadConfig.getMaxSize() + "MB");
        }

        // 校验文件类型
        String extension = FileUtil.getExtension(file.getOriginalFilename());
        boolean allowed = false;
        for (String type : uploadConfig.getAllowTypes()) {
            if (type.equalsIgnoreCase(extension)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new UploadException("invalid file type");
        }
    }
}