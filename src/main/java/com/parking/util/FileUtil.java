package com.parking.util;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex > -1 && dotIndex < fileName.length()) ?
                fileName.substring(dotIndex + 1).toLowerCase() : "";
    }

    /**
     * 获取日期路径
     */
    public static String getDatePath() {
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        return sdf.format(new Date());
    }
}