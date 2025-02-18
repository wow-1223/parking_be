package com.parking.controller.common;

import com.parking.model.param.upload.UploadResponse;
import com.parking.service.upload.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/file")
    public UploadResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("上传文件: {}", file.getOriginalFilename());
        return UploadResponse.success(uploadService.uploadFile(file));
    }

    @PostMapping("/files")
    public UploadResponse<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传文件: {} 个", files.length);
        return UploadResponse.success(uploadService.uploadFiles(files));
    }

    @DeleteMapping
    public UploadResponse<Void> deleteFile(@RequestParam String fileUrl) {
        uploadService.deleteFile(fileUrl);
        return UploadResponse.success(null);
    }
}