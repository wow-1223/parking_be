package com.parking.model.param.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperationResponse extends BaseResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 车位id
     */
    private Long id;

    public static OperationResponse operationSuccess(Long id, String message) {
        OperationResponse response = new OperationResponse();
        response.setId(id);
        response.setCode(200);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
    
    public static OperationResponse operationFail(String message) {
        OperationResponse response = new OperationResponse();
        response.setCode(500);
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 