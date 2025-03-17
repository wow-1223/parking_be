package com.parking.service2.user;

import com.parking.enums.user.UserRoleEnum;
import com.parking.model.param.common.OperationResponse;

/**
 * 用户角色服务
 */
public interface RoleService {

    /**
     * 给用户分配角色
     */
    OperationResponse assignRole(Long userId, UserRoleEnum role);

}
