package com.parking.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final String errorCode;

    public PaymentException(String message) {
        super(message);
        this.errorCode = "PAY_ERROR";
    }

    public PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PAY_ERROR";
    }
}