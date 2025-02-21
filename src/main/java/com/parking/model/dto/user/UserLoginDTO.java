package com.parking.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO extends UserDTO {
    private String token;
    private Long userId;
    private String phone;
    private String nickName;
    private String avatarUrl;
}