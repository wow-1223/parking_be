package com.parking.controller.common;

import com.parking.model.param.upload.UploadResponse;
import com.parking.handler.upload.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 文件上传
     * @param file 文件
     * @return 文件上传结果
     */
    @PostMapping("/file")
    public UploadResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("upload file: {}", file.getOriginalFilename());
        return UploadResponse.success(uploadService.uploadFile(file));
    }

    /**
     * 文件批量上传
     * @param files 文件列表
     * @return 文件上传结果
     */
    @PostMapping("/files")
    public UploadResponse<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        log.info("batch upload file: {} 个", files.length);
        return UploadResponse.success(uploadService.uploadFiles(files));
    }

    /**
     * 文件删除
     * @param fileUrl 文件地址
     * @return 文件删除结果
     */
    @DeleteMapping
    public UploadResponse<Void> deleteFile(@RequestParam String fileUrl) {
        uploadService.deleteFile(fileUrl);
        return UploadResponse.success(null);
    }
}