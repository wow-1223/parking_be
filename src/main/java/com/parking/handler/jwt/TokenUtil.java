package com.parking.handler.jwt;

import org.springframework.security.core.context.SecurityContextHolder;

public class TokenUtil {

    public static Long getUserId() {
        return Long.valueOf((String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal());
    }
}
