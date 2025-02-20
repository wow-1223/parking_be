# API接口测试文档


## 用户认证接口
Base URL: `/api/user`

### 1. 微信登录
```http
POST /api/user/login/wechat/{code}
```

**路径参数:**
- code: 微信登录code

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "token": "eyJhbGciOiJIUzI1...",
        "userId": 123,
        "phone": "13800138000",
        "nickName": "用户昵称",
        "avatarUrl": "https://example.com/avatar.jpg"
    }
}
```

### 2. 手机号登录
```http
POST /api/user/login/phone
```

**请求参数:**
```json
{
    "phone": "13800138000",
    "password": "password123",  // 密码登录时必填
    "verifyCode": "123456"     // 验证码登录时必填
}
```

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "token": "eyJhbGciOiJIUzI1...",
        "userId": 123,
        "phone": "13800138000",
        "nickName": "用户昵称",
        "avatarUrl": "https://example.com/avatar.jpg"
    }
}
```

**测试命令:**
```bash
# 密码登录
curl -X POST 'http://localhost:8080/api/user/login/phone' \
-H 'Content-Type: application/json' \
-d '{
    "phone": "13800138000",
    "password": "password123"
}'

# 验证码登录
curl -X POST 'http://localhost:8080/api/user/login/phone' \
-H 'Content-Type: application/json' \
-d '{
    "phone": "13800138000",
    "verifyCode": "123456"
}'
```

### 3. 发送验证码
```http
POST /api/user/sendVerifyCode
```

**请求参数:**
- phone: 手机号(query参数)

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "id": 0,
        "message": "send verify code success"
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/user/sendVerifyCode?phone=13800138000'
```

### 4. 用户注册
```http
POST /api/user/register
```

**请求参数:**
```json
{
    "phone": "13800138000",
    "password": "password123",
    "verifyCode": "123456"
}
```

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "id": 123,
        "message": "register success"
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/user/register' \
-H 'Content-Type: application/json' \
-d '{
    "phone": "13800138000",
    "password": "password123",
    "verifyCode": "123456"
}'
```

## 一、用户订单接口

### 1.1 创建订单
- 接口: POST /api/orders/createOrder
- 描述: 创建新的停车订单

请求参数:
```json
{
    "userId": 1,
    "parkingSpotsId": 1,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00",
    "carNumber": "沪A12345",
    "amount": 20.00
}
```

测试命令:
```bash
curl -X POST 'http://localhost:8080/api/orders/createOrder' \
-H 'Content-Type: application/json' \
-H 'Authorization: Bearer ${TOKEN}' \
-d '{
    "userId": 1,
    "parkingSpotsId": 1,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00",
    "carNumber": "沪A12345",
    "amount": 20.00
}'
```

### 1.2 获取订单列表
- 接口: GET /api/orders/getOrders
- 描述: 分页查询用户订单列表

请求参数:
- userId: 用户ID (必填)
- status: 订单状态 (可选)
- page: 页码，默认1
- size: 每页大小，默认20

测试命令:
```bash
curl 'http://localhost:8080/api/orders/getOrders?userId=1&page=1&size=20' \
-H 'Authorization: Bearer ${TOKEN}'
```

### 1.3 取消订单
- 接口: POST /api/orders/cancelOrder/{id}
- 描述: 取消指定订单

测试命令:
```bash
curl -X POST 'http://localhost:8080/api/orders/cancelOrder/1' \
-H 'Authorization: Bearer ${TOKEN}'
```

## 二、用户停车位接口

### 2.1 获取附近停车位
- 接口: POST /api/user/parking/nearby
- 描述: 获取附近可用的停车位

请求参数:
```json
{
  "longitude": 121.368116,
  "latitude": 31.035439,
  "radius": 4000,
  "price": 100,
  "startTime": "2024-03-21 10:00:00",
  "endTime": "2024-03-21 16:00:00",
  "page": 1,
  "size": 10
}
```

### 2.2 获取停车位详情
- 接口: POST /api/user/parking/{id}/{startTime}/{endTime}
- 描述: 获取停车位详细信息

请求参数:
```json
{
    "id": 920,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 13:00:00"
}
```

## 三、完整测试脚本

```bash
#!/bin/bash

# 测试环境配置
BASE_URL="http://localhost:8080"
TOKEN="your-jwt-token-here"

# 1. 获取附近停车位
echo "Testing nearby parking spots..."
curl -X POST "${BASE_URL}/api/parking/nearby" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ${TOKEN}" \
-d '{
    "longitude": 121.4737,
    "latitude": 31.2304,
    "radius": 1000,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00",
    "page": 1,
    "size": 10
}'
echo -e "\n"

# 2. 获取停车位详情
echo "Testing get parking spot detail..."
curl "${BASE_URL}/api/parking/1/2024-03-21%2010:00:00/2024-03-21%2012:00:00" \
-H "Authorization: Bearer ${TOKEN}"
echo -e "\n"

# 3. 创建订单
echo "Testing create order..."
curl -X POST "${BASE_URL}/api/orders/createOrder" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ${TOKEN}" \
-d '{
    "userId": 1,
    "parkingSpotsId": 1,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00",
    "carNumber": "沪A12345",
    "amount": 20.00
}'
echo -e "\n"

# 4. 查询订单列表
echo "Testing get orders..."
curl "${BASE_URL}/api/orders/getOrders?userId=1&page=1&size=20" \
-H "Authorization: Bearer ${TOKEN}"
echo -e "\n"

# 5. 取消订单
echo "Testing cancel order..."
curl -X POST "${BASE_URL}/api/orders/cancelOrder/1" \
-H "Authorization: Bearer ${TOKEN}"
echo -e "\n"
```

## 四、响应状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 五、注意事项

1. 所有请求需要携带JWT token进行认证
2. 时间格式统一使用: yyyy-MM-dd HH:mm:ss
3. 金额单位为元，精确到分
4. 经纬度使用WGS84坐标系
5. 搜索半径单位为米
6. 所有请求都需要添加Authorization头部 