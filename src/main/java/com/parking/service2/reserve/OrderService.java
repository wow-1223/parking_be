package com.parking.service2.reserve;

import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.OperateOrderRequest;

public interface OrderService {

    /**
     * 创建订单
     */
    OperationResponse createOrder(OperateOrderRequest request);

    /**
     * 更新订单
     */
    OperationResponse updateOrder(OperateOrderRequest request);

    /**
     * 取消订单
     */
    OperationResponse cancelOrder(CancelOrderRequest request);


    /**
     * 订单被占用时补偿
     */
    void compensate(Order order);

}
