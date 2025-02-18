package com.parking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.parking.mapper.mybatis")
public class ParkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }
}