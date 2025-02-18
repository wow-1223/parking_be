package com.parking.model.param.upload;

import lombok.Data;

@Data
public class UploadResponse<T> {
    private boolean success;
    private String message;

    private T data;

    public static <T> UploadResponse<T> success(T url) {
        UploadResponse<T> response = new UploadResponse<>();
        response.setSuccess(true);
        response.setData(url);
        return response;
    }

    public static <T> UploadResponse<T> fail(String message) {
        UploadResponse<T> response = new UploadResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}