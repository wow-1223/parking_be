package com.parking.service2.user.impl;

import com.parking.enums.user.UserRoleEnum;
import com.parking.model.param.common.OperationResponse;
import com.parking.service2.user.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public OperationResponse assignRole(Long userId, UserRoleEnum role) {
        return null;
    }
}
