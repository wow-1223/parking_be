package com.parking.model.dto.common;

import lombok.Data;

/**
 * 错误响应DTO
 */
@Data
public class ErrorResponse {
    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}