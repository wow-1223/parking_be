package com.parking.service.user;

import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.service.OrderService;

public interface UserOrderService extends OrderService {

    OperationResponse createOrder(CreateOrderRequest request);

    OperationResponse cancelOrder(OperateOrderRequest request);

    OperationResponse completeOrder(OperateOrderRequest request);

    void handlePaySuccess(Order order, PayNotifyVO notify);

    void handlePayRefunded(Order order, PayNotifyVO notify);
}