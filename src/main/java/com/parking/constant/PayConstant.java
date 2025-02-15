package com.parking.constant;

public class PayConstant {

    public static class PayError {

        // 无效的支付类型
        public static final String INVALID_PAY_TYPE = "INVALID_PAY_TYPE";

        // 无效的签名
        public static final String INVALID_SIGN = "INVALID_SIGN";

        // 签名错误
        public static final String INVALID_SIGNATURE = "INVALID_SIGNATURE";
        // 缺少必填参数
        public static final String MISSING_PARAMETER = "MISSING_PARAMETER";
        // 订单已支付
        public static final String ORDER_PAID = "ORDER_PAID";
        // 订单已关闭
        public static final String ORDER_CLOSED = "ORDER_CLOSED";
        // 订单不存在
        public static final String ORDER_NOT_EXIST = "ORDER_NOT_EXIST";
        // 订单金额不一致
        public static final String AMOUNT_INCONSISTENT = "AMOUNT_INCONSISTENT";

        public static final String AMOUNT_EXPIRED = "AMOUNT_EXPIRED";

        // ... 其他错误码
        public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    }

    /**
     * 支付状态
     */
    public static class PayStatus {
        // 待支付
        public static final String PENDING = "PENDING";
        // 支付成功
        public static final String SUCCESS = "SUCCESS";
        // 支付失败
        public static final String FAILED = "FAILED";
        // 已关闭
        public static final String CLOSED = "CLOSED";
        // 已退款
        public static final String REFUNDED = "REFUNDED";
    }

    /**
     * 交易类型
     */
    public static class TradeType {
        // 小程序支付
        public static final String JSAPI = "JSAPI";
        // APP支付
        public static final String APP = "APP";
        // H5支付
        public static final String H5 = "H5";
        // 扫码支付
        public static final String NATIVE = "NATIVE";
    }

    /**
     * 退款状态
     */
    public static class RefundStatus {
        // 退款处理中
        public static final String PROCESSING = "PROCESSING";
        // 退款成功
        public static final String SUCCESS = "SUCCESS";
        // 退款失败
        public static final String FAILED = "FAILED";
        // 退款关闭
        public static final String CLOSED = "CLOSED";
    }
}