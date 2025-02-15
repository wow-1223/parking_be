package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "pay_notify_log")
public class PayNotifyLog {

    @TableId
    private Long id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 支付平台交易号
     */
    private String tradeNo;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 通知参数
     */
    private String notifyParams;

    /**
     * 处理状态：SUCCESS/FAILED
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}