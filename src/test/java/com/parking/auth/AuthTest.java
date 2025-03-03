package com.parking.auth;

import com.parking.handler.jwt.JwtUtil;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthTest {

    @Resource
    private JwtUtil jwtUtil;

    @Test
    public void testGenToken() {
        System.out.println(jwtUtil.generateToken(33L));
    }
}
