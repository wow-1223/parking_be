package com.parking.util.tool;

import com.parking.exception.UploadException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtil {

    /**
     * 压缩图片
     * @param imageData 原图片数据
     * @param fileName 文件名
     * @param maxWidth 最大宽度
     * @return 压缩后的图片数据
     */
    public static byte[] compress(byte[] imageData, String fileName, int maxWidth) {
        try {
            // 如果不是图片，直接返回
            if (!isImage(fileName)) {
                return imageData;
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 使用Thumbnails进行压缩
            Thumbnails.of(inputStream)
                    .size(maxWidth, maxWidth)
                    .keepAspectRatio(true)
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new UploadException("压缩图片失败", e);
        }
    }

    /**
     * 判断是否为图片
     */
    public static boolean isImage(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        String extension = FileUtil.getExtension(fileName).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension)
                || "png".equals(extension) || "gif".equals(extension);
    }
}