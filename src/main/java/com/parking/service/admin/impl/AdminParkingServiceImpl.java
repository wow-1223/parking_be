package com.parking.service.admin.impl;

import com.parking.handler.encrypt.AesUtil;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.admin.request.AdminParkingRequest;
import com.parking.model.param.common.PageResponse;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.service.admin.AdminParkingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminParkingServiceImpl implements AdminParkingService {

    @Resource
    private ParkingSpotRepository parkingSpotRepository;

    @Resource
    private AesUtil aesUtil;

    @Override
    public PageResponse<ParkingSpotDTO> getParkingList(AdminParkingRequest request) {

        return null;
    }
}
