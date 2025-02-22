package com.parking.model.vo.pay;

import com.parking.enums.PayTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayNotifyVO {
    private String orderId;
    private String tradeNo;
    private String status;
    private Long amount;
    private String notifyTime;
    private PayTypeEnum payType;
}
