package com.parking.event;

import com.parking.enums.PayTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PayNotifyEvent extends ApplicationEvent {

    private final String orderId;
    private final String tradeNo;
    private final String status;
    private final Long amount;
    private final String notifyTime;
    private final PayTypeEnum payType;

    public PayNotifyEvent(Object source, String orderId, String tradeNo, String status,
                          Long amount, String notifyTime, PayTypeEnum payType) {
        super(source);
        this.orderId = orderId;
        this.tradeNo = tradeNo;
        this.payType = payType;
        this.status = status;
        this.amount = amount;
        this.notifyTime = notifyTime;
    }
}