package com.parking.factory;

import com.parking.constant.PayConstant;
import com.parking.enums.PayTypeEnum;
import com.parking.exception.PaymentException;
import com.parking.service.payment.impl.AliPayServiceImpl;
import com.parking.service.payment.PayService;
import com.parking.service.payment.impl.WechatPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PayServiceFactory {

    private final Map<PayTypeEnum, PayService> payServices = new HashMap<>();

    @Autowired
    public PayServiceFactory(AliPayServiceImpl alipayService, WechatPayServiceImpl wechatPayService) {
        payServices.put(PayTypeEnum.ALIPAY, alipayService);
        payServices.put(PayTypeEnum.WECHAT_PAY, wechatPayService);
    }

    /**
     * 获取支付服务
     */
    public PayService getPayService(PayTypeEnum payType) {
        PayService payService = payServices.get(payType);
        if (payService == null) {
            throw new PaymentException(PayConstant.PayError.INVALID_PAY_TYPE, "invalid pay type: " + payType.getValue());
        }
        return payService;
    }
}