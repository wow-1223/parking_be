package com.parking.model.param.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "用户ID不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3 到 20 之间")
    private String code;
}