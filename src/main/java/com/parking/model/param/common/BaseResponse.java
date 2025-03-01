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
    private Integer code;

    /**
     * 错误信息
     */
    private String message;
}
