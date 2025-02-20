package com.parking.model.param.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailResponse<T> extends BaseResponse {

    private T data;

    public static <T> DetailResponse<T> detailSuccess(T data, String message) {
        DetailResponse<T> response = new DetailResponse<>();
        response.setCode("200");
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static <T> DetailResponse<T> detailFail(String message) {
        DetailResponse<T> response = new DetailResponse<>();
        response.setCode("500");
        response.setMessage(message);
        return response;
    }
}
