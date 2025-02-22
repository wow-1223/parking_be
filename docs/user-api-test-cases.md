# API 接口文档及测试用例

```bash
api domain: http://localhost:8080
eg. http://localhost:8080/api/user/register


mysql:
  host: 139.224.209.172
  url: jdbc:mysql://139.224.209.172:3306
  username: zc
  password: 123456
  port: 3306
  database: xiaoxin_parking
  dbname: wow_test_db
  

redis:
  host: dbconn.sealosgzg.site
  port: 35471
  url: jdbc:redis://dbconn.sealosgzg.site:35471/0
  username: zc
  password: 123456
  database: 0
```

## 1. 用户认证接口 (/api/user)

### 1.1 发送验证码
- 接口: POST /api/user/sendVerifyCode
- 描述: 发送手机验证码
- 请求参数: phone (手机号)
- 响应结果:
```json
{
  "code": "200",
  "message": "send verify code success",
  "success": true,
  "data": "038699"  // 测试时返回，生产时短信验证码发送至用户手机
}
```

测试用例:
```bash
curl -X POST 'http://localhost:8080/api/user/sendVerifyCode?phone=13812345678'
```

### 1.2 用户注册
- 接口: POST /api/user/register
- 描述: 用户注册
- 请求参数:
```json
{
  "phone": "13812345678",
  "password": "1234567890",
  "verifyCode": "038699"
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "register success",
  "success": true,
  "data": 1892922997525282817 // userId
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H 'Content-Type: application/json' \
  -d '{
    "phone": "13812345678",
    "password": "123456",
    "verifyCode": "123456"
  }'
```

### 1.3 手机号登录
- 接口: POST /api/user/login/phone
- 描述: 使用手机号+验证码或密码登录
- 请求参数:
```json
{
  "phone": "13812345678",
  "verifyCode": "482104"
}
``` 
``` json
{
  "phone": "13812345678",
  "password": "1234567890"
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "login success",
  "success": true,
  "data": {
    "id": null,
    "name": null,
    "phone": "13812345678",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODkyOTIyOTk3NTI1MjgyODE3IiwiaWF0IjoxNzQwMTQzMDYxLCJleHAiOjE3NDAyMjk0NjF9.f0PLtPv179aKHk_oeq_YWrFTT6eWc7OqliFbn8vISvocHgfBvjp6x2-EzKHqWSgWPn31xhg0Yw4qSane5sW1Rg",
    "userId": 1892922997525282817,
    "nickName": null,
    "avatarUrl": null
  }
}
```

测试用例:
```bash
# 验证码登录
curl -X POST http://localhost:8080/api/user/login/phone \
  -H 'Content-Type: application/json' \
  -d '{
    "phone": "13812345678",
    "verifyCode": "123456"
  }'

# 密码登录
curl -X POST http://localhost:8080/api/user/login/phone \
  -H 'Content-Type: application/json' \
  -d '{
    "phone": "13812345678",
    "password": "123456"
  }'
```

### 1.4 用户微信登录(未注册用户会直接注册)
- 接口: POST /api/user/login/wechat/{code}
- 描述: 用户微信登录
- 响应结果:
```json
{
  "code": "200",
  "message": "login success",
  "success": true,
  "data": {
    "id": null,
    "name": null,
    "phone": null,
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODkyOTI1Mjg4ODE5MDMyMDY2IiwiaWF0IjoxNzQwMTQzNTMzLCJleHAiOjE3NDAyMjk5MzN9.8SRpagK9Qte2tGAR82z_cNsQ9V6bO-pR6rTRqC5top56HfTz3sE4LZGk5FuoCZTLJBFxAQYTQCEi7tC2LpeWpw",
    "userId": 1892925288819032066,
    "nickName": null,
    "avatarUrl": null
  }
}
```
测试用例:
```bash
# 微信登录
curl -X POST http://localhost:8080/api/user/login/wechat/wx_code_01
```

## 2. 用户停车位接口 (/api/user/parking)

### 2.1 获取附近停车位
- 接口: POST /api/user/parking/nearby
- 描述: 获取用户附近的可用停车位
- 请求参数:
```json
{
    "longitude": 121.368116,  // 经度
    "latitude": 31.035439,    // 纬度
    "radius": 3000,          // 搜索半径(米)
    "price": 50,             // 最高价格(可选)
    "startTime": "2024-03-21 10:00:00",  // 开始时间
    "endTime": "2024-03-21 12:00:00",    // 结束时间
    "page": 1,              // 页码
    "size": 20             // 每页数量
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": null,
  "total": 5,
  "list": [
    {
      "id": 76,
      "latitude": 31.139342,
      "longitude": 121.110764,
      "location": "上海市测试地址76",
      "price": 6.52
    },
    {
      "id": 440,
      "latitude": 31.136165,
      "longitude": 121.113167,
      "location": "上海市测试地址440",
      "price": 5.55
    },
    {
      "id": 502,
      "latitude": 31.137019,
      "longitude": 121.128981,
      "location": "上海市测试地址502",
      "price": 29.58
    },
    {
      "id": 673,
      "latitude": 31.133552,
      "longitude": 121.122662,
      "location": "上海市测试地址673",
      "price": 29.18
    },
    {
      "id": 937,
      "latitude": 31.106708,
      "longitude": 121.104509,
      "location": "上海市测试地址937",
      "price": 23.83
    }
  ]
}
```

测试用例:
```bash
curl -X POST 'http://localhost:8080/api/user/parking/nearby' \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
    "longitude": 121.123456,
    "latitude": 31.123456,
    "radius": 3000,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00",
    "page": 1,
    "size": 20
  }'
```

### 2.2 获取停车位详情
- 接口: POST /api/user/parking/detail
- 描述: 获取停车位详细信息
- 请求参数:
```json
{
  "id": 920,
  "startTime": "2024-03-21 10:00:00",
  "endTime": "2024-03-21 13:00:00"
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "get detail success",
  "data": {
    "id": 920,
    "latitude": 31.054138,
    "longitude": 121.343743,
    "location": "上海市测试地址920",
    "price": 17.75,
    "description": "测试停车位描述920",
    "images": [
      "https://example.com/parking/spot920_1.jpg",
      "https://example.com/parking/spot920_2.jpg"
    ],
    "facilities": [
      "CAMERA",
      null,
      null,
      null
    ],
    "owner": {
      "id": 831,
      "name": "用户831",
      "phone": "13812345678"
    },
    "parkingIntervals": [
      {
        "startTime": "06:11:00",
        "endTime": "20:38:00"
      }
    ],
    "occupiedIntervals": []
  }
}
```

测试用例:
```bash
curl -X POST 'http://localhost:8080/api/user/parking/detail' \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
        "id": 920,
        "startTime": "2024-03-21 10:00:00",
        "endTime": "2024-03-21 13:00:00"
    }'
```

## 3. 用户订单接口 (/api/user/orders)

### 3.1 获取订单列表
- 接口: POST /api/user/orders/getOrders
- 描述: 获取用户订单列表
- 请求参数:
```json
{
    "userId": 1,
    "status": null,           // 订单状态(可选)
    "page": 1,
    "size": 20
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": null,
  "total": 2,
  "list": [
    {
      "id": 1892608856319176706,
      "userId": 1,
      "parkingSpotId": 1,
      "ownerId": 938,
      "location": "上海市测试地址1",
      "longitude": 121.553078,
      "latitude": 31.009558,
      "occupiedSpotId": null,
      "startTime": null,
      "endTime": null,
      "carNumber": "\"沪A12345\"",
      "amount": 31.80,
      "refundAmount": 0.00,
      "transactionId": null,
      "status": 5
    },
    {
      "id": 344,
      "userId": 1,
      "parkingSpotId": 331,
      "ownerId": 830,
      "location": "上海市测试地址331",
      "longitude": 121.82588,
      "latitude": 31.272399,
      "occupiedSpotId": 344,
      "startTime": "2024-04-07T14:26:52",
      "endTime": "2024-04-07T17:00:29",
      "carNumber": "沪H45136",
      "amount": 858.00,
      "refundAmount": 0.00,
      "transactionId": "wx81411c285c3a80697f7045dc6bcc",
      "status": 4
    }
  ]
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/user/orders/getOrders?userId=1&page=1&size=20' \
  -H 'Authorization: Bearer your_token_here'
```

### 3.2 创建订单
- 接口: POST /api/user/orders/createOrder
- 描述: 创建订单
- 请求参数:
```json
{
  "userId": 2,
  "parkingSpotId": 822,
  "startTime": "2024-03-21 10:00:00",
  "endTime": "2024-03-21 12:00:00",
  "carNumber": "沪A12346",
  "amount": 20.00
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "create success",
  "success": true,
  "id": 1892934248510853122
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/user/orders/createOrder \
  -H 'Content-Type: application/json' \
  -d '{
        "userId": 2,
        "parkingSpotId": 822,
        "startTime": "2024-03-21 10:00:00",
        "endTime": "2024-03-21 12:00:00",
        "carNumber": "沪A12346",
        "amount": 20.00
    }'
```

### 3.3 取消订单
- 接口: POST /api/user/orders/cancelOrder
- 描述: 取消订单
- 请求参数:
```json
{
  "userId": 2,
  "orderId": 1892934248510853122
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "cancel success",
  "success": true,
  "id": 1892934248510853122
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/user/orders/cancelOrder \
  -H 'Content-Type: application/json' \
  -d '{
      "userId": 2,
      "orderId": 1892934248510853122
    }'
```

## 4. 用户收藏接口 (/api/user/favorites)

### 4.1 添加/取消收藏
- 接口: POST /api/user/favorites/toggleFavorite
- 描述: 收藏/取消收藏停车位
- 请求参数:
```json
{
    "userId": 1,
    "parkingSpotId": 1,
    "action": true
}
```
```json
{
  "id": 1025, // favoriteId
  "action": false
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "add favorite success",
  "success": true,
  "id": 1892935989646135297
}
```
```json
{
    "code": "200",
    "message": "delete favorite success",
    "success": true,
    "id": 1892935989646135297
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/user/favorites/toggleFavorite \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "parkingSpotId": 1,
    "action": true
  }'
```
```bash
curl -X POST http://localhost:8080/api/user/favorites/toggleFavorite \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1892935989646135297,
    "action": false
  }'
```

### 4.2 获取收藏列表
- 接口: GET /api/user/favorites/getFavorites
- 描述: 获取用户收藏的停车位列表
- 请求参数: userId, page, size (分页参数)
- 响应结果:
```json
{
  "code": "200",
  "message": null,
  "total": 2,
  "list": [
    {
      "id": 1,
      "latitude": 31.009558,
      "longitude": 121.553078,
      "location": "上海市测试地址1",
      "price": 15.90
    },
    {
      "id": 25,
      "latitude": 31.308947,
      "longitude": 121.877927,
      "location": "上海市测试地址25",
      "price": 16.39
    }
  ]
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/user/favorites/getFavorites?userId=1&page=1&size=20' \
  -H 'Authorization: Bearer your_token_here'
```

## 7. 错误码说明

| 错误码 | 说明      |
|-----|---------|
| 200 | 成功      |
| 400 | 请求参数错误  |
| 401 | 未授权     |
| 402 | 业务异常    |
| 403 | 禁止访问    |
| 404 | 资源不存在   |
| 500 | 服务器内部错误 |
| 901 | 支付错误    |

## 8. 订单状态说明

| 状态码 | 说明 |
|--------|------|
| 0 | 待支付 |
| 1 | 已支付 |
| 2 | 已取消 |
| 3 | 已完成 |
| 4 | 退款中 |
| 5 | 已退款 |
| 6 | 已过期 |

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带token:
```
Authorization: Bearer your_token_here
```

2. 时间格式统一使用: yyyy-MM-dd HH:mm:ss

3. 经纬度使用浮点数，保留6位小数

4. 金额单位为元，保留2位小数

5. 分页参数: page从1开始，size默认20

6. 文件上传大小限制: 10MB

7. 支持的文件类型: jpg, jpeg, png, gif 