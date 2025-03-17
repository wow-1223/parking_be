package com.parking.service2.user.impl;

import com.parking.model.param.common.OperationResponse;
import com.parking.service2.user.MarginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class MarginServiceImpl implements MarginService {

    @Override
    public OperationResponse payMargin(Long userId, BigDecimal margin) {
        return null;
    }

    @Override
    public OperationResponse returnMargin(Long userId, BigDecimal margin) {
        return null;
    }

}
