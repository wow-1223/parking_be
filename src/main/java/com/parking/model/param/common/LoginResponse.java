package com.parking.model.param.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse<T> extends BaseResponse {

    private boolean success;

    private T data;

    public static <T> LoginResponse<T> loginSuccess(T data, String message) {
        LoginResponse<T> response = new LoginResponse<>();
        response.setCode("200");
        response.setData(data);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static <T> LoginResponse<T> loginFail(String code, String message) {
        LoginResponse<T> response = new LoginResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
