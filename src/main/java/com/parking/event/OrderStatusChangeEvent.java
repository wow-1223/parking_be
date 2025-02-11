package com.parking.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.Map;

@Getter
public class OrderStatusChangeEvent extends ApplicationEvent {

    private final String orderId;
    private final String status;
    private final Map<String, Object> paymentInfo;

    public OrderStatusChangeEvent(String orderId, String status, Map<String, Object> paymentInfo) {
        super(orderId);
        this.orderId = orderId;
        this.status = status;
        this.paymentInfo = paymentInfo;
    }
}