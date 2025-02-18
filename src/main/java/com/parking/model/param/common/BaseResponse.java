package com.parking.model.param.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    public static BaseResponse success(String message) {
        BaseResponse response = new BaseResponse();
        response.setCode("200");
        response.setMessage(message);
        return response;
    }

    public static BaseResponse fail(String code, String message) {
        BaseResponse response = new BaseResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

}
