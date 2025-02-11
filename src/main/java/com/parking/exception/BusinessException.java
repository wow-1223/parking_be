package com.parking.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}