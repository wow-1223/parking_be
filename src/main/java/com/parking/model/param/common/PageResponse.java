package com.parking.model.param.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> extends BaseResponse {
    private long total;
    private List<T> list;

    public static <T> PageResponse<T> pageSuccess(List<T> list, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setCode(200);
        response.setTotal(total);
        response.setList(list);
        return response;
    }

    public static <T> PageResponse<T> pageFail(Integer code, String message) {
        PageResponse<T> response = new PageResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}