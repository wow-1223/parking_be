package com.parking.service2.reserve.impl;

import com.google.common.collect.Sets;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service2.reserve.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.parking.enums.order.OrderStatusEnum.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private static final Set<Integer> ALLOW_CANCEL_STATUS = Sets.newHashSet(
            PENDING_PAYMENT.getStatus(),
            RESERVED.getStatus(),
            USER_OCCUPIED.getStatus(),
            UNKNOWN_OCCUPIED.getStatus()
    );

    private static final Set<Integer> ALLOW_COMPLETE_STATUS = Sets.newHashSet(
            PROCESSING.getStatus(),
            LEAVE_TEMPORARILY.getStatus(),
            TIMEOUT.getStatus()
    );

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ParkingSpotRepository parkingSpotRepository;

    @Autowired
    protected OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    protected AesUtil aesUtil;

    @Override
    public OperationResponse createOrder(OperateOrderRequest request) {
        return null;
    }

    @Override
    public OperationResponse updateOrder(OperateOrderRequest request) {
        return null;
    }

    @Override
    public OperationResponse cancelOrder(CancelOrderRequest request) {
        return null;
    }

    @Override
    public void compensate(Order order) {

    }
}
