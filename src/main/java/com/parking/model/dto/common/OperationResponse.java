package com.parking.model.dto.common;

import lombok.Data;

@Data
public class OperationResponse {
    private boolean success;
    private String message;
    
    public static OperationResponse success(String message) {
        OperationResponse response = new OperationResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
    
    public static OperationResponse fail(String message) {
        OperationResponse response = new OperationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 