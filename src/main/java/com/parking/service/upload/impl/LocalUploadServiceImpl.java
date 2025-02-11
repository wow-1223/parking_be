package com.parking.service.upload.impl;

import com.parking.config.UploadConfig;
import com.parking.exception.UploadException;
import com.parking.service.upload.UploadService;
import com.parking.util.FileUtil;
import com.parking.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "upload.use-cloud", havingValue = "false", matchIfMissing = true)
public class LocalUploadServiceImpl implements UploadService {

    @Autowired
    private UploadConfig uploadConfig;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // 校验文件
            validateFile(file);

            // 压缩图片
            byte[] imageData = ImageUtil.compress(file.getBytes(),
                    file.getOriginalFilename(), 1024);

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.getExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + "." + extension;

            // 创建目录
            String datePath = FileUtil.getDatePath();
            File uploadDir = new File(uploadConfig.getUploadPath() + datePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            String filePath = uploadDir.getPath() + File.separator + fileName;
            FileUtils.writeByteArrayToFile(new File(filePath), imageData);

            // 返回访问URL
            return "/upload" + datePath + "/" + fileName;

        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new UploadException("上传文件失败: " + e.getMessage());
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
        try {
            String filePath = uploadConfig.getUploadPath() + fileUrl.replace("/upload", "");
            File file = new File(filePath);
            if (file.exists()) {
                boolean success = file.delete();
                if (!success) {
                    throw new UploadException("删除文件失败");
                }
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new UploadException("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        // 校验文件大小
        long size = file.getSize();
        if (size > uploadConfig.getMaxSize() * 1024 * 1024) {
            throw new UploadException("文件大小不能超过" + uploadConfig.getMaxSize() + "MB");
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
            throw new UploadException("不支持的文件类型");
        }
    }
}