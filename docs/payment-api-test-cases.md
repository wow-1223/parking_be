# 支付接口文档及测试用例

## 1. 创建支付订单

### 1.1 支付宝支付
- 接口: POST /api/pay/create
- 描述: 创建支付宝支付订单
- 请求参数:
```json
{
    "orderId": "202403211234567",
    "amount": 1000,              // 金额(分)
    "description": "停车费用",    // 商品描述
    "payType": "ALIPAY",        // 支付方式
    "productCode": "QUICK_WAP_WAY",  // 产品码
    "returnUrl": "https://example.com/return",  // 支付完成跳转地址(可选)
    "notifyUrl": "https://example.com/notify"   // 支付结果通知地址(可选)
}
```

- 响应结果:
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211234567",
        "payUrl": "https://openapi.alipay.com/gateway.do?...",  // 支付链接
        "status": "PENDING"
    }
}
```

测试用例:
```bash
# 创建支付宝支付订单
curl -X POST 'http://localhost:8080/api/pay/create' \
  -H 'Content-Type: application/json' \
  -d '{
    "orderId": "202403211234567",
    "amount": 1000,
    "description": "停车费用",
    "payType": "ALIPAY",
    "productCode": "QUICK_WAP_WAY"
  }'
```

### 1.2 微信支付
- 接口: POST /api/pay/create
- 描述: 创建微信支付订单
- 请求参数:
```json
{
    "orderId": "202403211234567",
    "amount": 1000,              // 金额(分)
    "description": "停车费用",    // 商品描述
    "payType": "WECHAT_PAY",    // 支付方式
    "openId": "oWx_123456789",  // 用户openid(jsapi支付必填)
    "tradeType": "JSAPI"        // 交易类型(JSAPI/NATIVE/APP/H5)
}
```

- 响应结果:
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211234567",
        "prepayId": "wx123456789",    // 预支付ID
        "payParams": {                 // JSAPI支付参数
            "appId": "wx123456789",
            "timeStamp": "1621234567",
            "nonceStr": "abcdef123456",
            "package": "prepay_id=wx123456789",
            "signType": "RSA",
            "paySign": "abcdef123456"
        }
    }
}
```

测试用例:
```bash
# 创建微信支付订单
curl -X POST 'http://localhost:8080/api/pay/create' \
  -H 'Content-Type: application/json' \
  -d '{
    "orderId": "202403211234567",
    "amount": 1000,
    "description": "停车费用",
    "payType": "WECHAT_PAY",
    "openId": "oWx_123456789",
    "tradeType": "JSAPI"
  }'
```

## 2. 查询支付订单

- 接口: GET /api/pay/query/{orderId}
- 描述: 查询支付订单状态
- 请求参数:
    - orderId: 订单ID
    - payType: 支付方式(ALIPAY/WECHAT_PAY)

- 响应结果:
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211234567",
        "status": "SUCCESS",          // 支付状态
        "amount": 1000,               // 支付金额(分)
        "payTime": "2024-03-21 12:34:56"  // 支付时间
    }
}
```

测试用例:
```bash
# 查询支付宝订单
curl -X GET 'http://localhost:8080/api/pay/query/202403211234567?payType=ALIPAY'

# 查询微信支付订单
curl -X GET 'http://localhost:8080/api/pay/query/202403211234567?payType=WECHAT_PAY'
```

## 3. 申请退款

- 接口: POST /api/pay/refund/{orderId}
- 描述: 申请订单退款
- 请求参数:
```json
{
    "payType": "ALIPAY",        // 支付方式
    "amount": 1000,             // 退款金额(分)
    "reason": "用户取消订单"     // 退款原因
}
```

- 响应结果:
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211234567",
        "status": "REFUNDED",         // 退款状态
        "refundAmount": 1000,         // 退款金额(分)
        "refundTime": "2024-03-21 12:34:56"  // 退款时间
    }
}
```

测试用例:
```bash
# 申请支付宝退款
curl -X POST 'http://localhost:8080/api/pay/refund/202403211234567' \
  -H 'Content-Type: application/json' \
  -d '{
    "payType": "ALIPAY",
    "amount": 1000,
    "reason": "用户取消订单"
  }'

# 申请微信退款
curl -X POST 'http://localhost:8080/api/pay/refund/202403211234567' \
  -H 'Content-Type: application/json' \
  -d '{
    "payType": "WECHAT_PAY",
    "amount": 1000,
    "reason": "用户取消订单"
  }'
```

## 4. 支付回调接口

### 4.1 支付宝支付结果通知
- 接口: POST /api/pay/notify/alipay
- 描述: 接收支付宝支付结果通知
- 请求参数: 支付宝回调参数(表单格式)
- 响应结果: success/fail

测试用例:
```bash
# 模拟支付宝支付成功回调
curl -X POST 'http://localhost:8080/api/pay/notify/alipay' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'out_trade_no=202403211234567&trade_no=2024032122001123456789&trade_status=TRADE_SUCCESS&total_amount=10.00&gmt_payment=2024-03-21 12:34:56'
```

### 4.2 微信支付结果通知
- 接口: POST /api/pay/notify/wechatpay
- 描述: 接收微信支付结果通知
- 请求参数: 微信回调参数(JSON格式)
- 响应结果:
```json
{
    "code": "SUCCESS",
    "message": "成功"
}
```

测试用例:
```bash
# 模拟微信支付成功回调
curl -X POST 'http://localhost:8080/api/pay/notify/wechatpay' \
  -H 'Content-Type: application/json' \
  -H 'Wechatpay-Signature: xxxxx' \
  -H 'Wechatpay-Timestamp: 1621234567' \
  -H 'Wechatpay-Nonce: abcdef123456' \
  -H 'Wechatpay-Serial: serial123456' \
  -d '{
    "id": "EV-2024032112345678",
    "create_time": "2024-03-21T12:34:56+08:00",
    "resource_type": "encrypt-resource",
    "event_type": "TRANSACTION.SUCCESS",
    "resource": {
      "algorithm": "AEAD_AES_256_GCM",
      "ciphertext": "...",
      "nonce": "...",
      "associated_data": ""
    }
  }'
```

## 5. 支付状态说明

| 状态码 | 说明 |
|--------|------|
| PENDING | 待支付 |
| SUCCESS | 支付成功 |
| FAILED | 支付失败 |
| CLOSED | 已关闭 |
| REFUNDED | 已退款 |

## 6. 注意事项

1. 金额单位统一使用分
2. 订单号格式: 年月日时分秒+3位随机数
3. 支付宝支付目前支持手机网站支付
4. 微信支付支持JSAPI、Native、APP、H5支付
5. 退款金额不能超过实际支付金额
6. 订单创建后15分钟未支付自动关闭
7. 支付成功后不可重复支付
8. 已支付订单才可申请退款

## 7. 常见问题

1. 支付失败：
    - 检查支付参数是否正确
    - 检查签名是否正确
    - 检查订单状态是否正常

2. 回调处理：
    - 验证签名
    - 检查订单金额
    - 检查订单状态
    - 防止重复处理
    - 返回正确格式的响应

3. 退款失败：
    - 检查订单状态
    - 检查退款金额
    - 检查账户余额 