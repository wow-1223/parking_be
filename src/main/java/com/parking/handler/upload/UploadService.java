package com.parking.handler.upload;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UploadService {

    /**
     * 上传单个文件
     */
    String uploadFile(MultipartFile file);

    /**
     * 批量上传文件
     */
    List<String> uploadFiles(MultipartFile[] files);

    /**
     * 删除文件
     */
    void deleteFile(String fileUrl);
}