package com.parking.service.user;

import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.service.OrderService;

public interface UserOrderService extends OrderService {

    OperationResponse createOrder(OperateOrderRequest request);

    OperationResponse updateOrder(OperateOrderRequest request);

    OperationResponse cancelOrder(CancelOrderRequest request);

    OperationResponse completeOrder(CancelOrderRequest request);

    void handlePaySuccess(Order order, PayNotifyVO notify);

    void handlePayRefunded(Order order, PayNotifyVO notify);
}