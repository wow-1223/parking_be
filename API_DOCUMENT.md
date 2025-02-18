# Parking Share API Documentation

## 目录
- [用户认证接口](#用户认证接口)
- [文件上传接口](#文件上传接口)
- [停车位接口](#停车位接口)
- [订单接口](#订单接口)

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

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/user/login/wechat/wx123456'
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

## 文件上传接口
Base URL: `/api/upload`

### 1. 单文件上传
```http
POST /api/upload/file
```

**请求参数:**
- file: 文件(form-data)

**响应示例:**
```json
{
    "code": 200,
    "data": "/upload/2024/03/21/f7c9bd42-1234-5678-90ab-cd1234567890.jpg"
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/upload/file' \
-H 'Content-Type: multipart/form-data' \
-F 'file=@/path/to/image.jpg'
```

### 2. 多文件上传
```http
POST /api/upload/files
```

**请求参数:**
- files: 文件数组(form-data)

**响应示例:**
```json
{
    "code": 200,
    "data": [
        "/upload/2024/03/21/f7c9bd42-1234-5678-90ab-cd1234567890.jpg",
        "/upload/2024/03/21/a1b2c3d4-5678-90ab-cdef-123456789012.jpg"
    ]
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/upload/files' \
-H 'Content-Type: multipart/form-data' \
-F 'files=@/path/to/image1.jpg' \
-F 'files=@/path/to/image2.jpg'
```

### 3. 删除文件
```http
DELETE /api/upload
```

**请求参数:**
- fileUrl: 文件URL(query参数)

**响应示例:**
```json
{
    "code": 200,
    "data": null
}
```

**测试命令:**
```bash
curl -X DELETE 'http://localhost:8080/api/upload?fileUrl=/upload/2024/03/21/image.jpg'
```

## 停车位接口
Base URL: `/api/parking`

### 1. 发布停车位
```http
POST /api/parking/spot
```

**请求参数:**
```json
{
    "name": "小区A-B1-123",
    "address": "上海市浦东新区xx路xx号",
    "latitude": 31.123456,
    "longitude": 121.123456,
    "price": 1000,  // 单位:分/小时
    "images": [
        "/upload/2024/03/21/image1.jpg",
        "/upload/2024/03/21/image2.jpg"
    ],
    "description": "地下停车场B1层123号",
    "availableStartTime": "08:00",
    "availableEndTime": "22:00"
}
```

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "id": 123,
        "message": "parking spot published successfully"
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/parking/spot' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...' \
-H 'Content-Type: application/json' \
-d '{
    "name": "小区A-B1-123",
    "address": "上海市浦东新区xx路xx号",
    "latitude": 31.123456,
    "longitude": 121.123456,
    "price": 1000,
    "images": [
        "/upload/2024/03/21/image1.jpg",
        "/upload/2024/03/21/image2.jpg"
    ],
    "description": "地下停车场B1层123号",
    "availableStartTime": "08:00",
    "availableEndTime": "22:00"
}'
```

### 2. 搜索附近停车位
```http
GET /api/parking/spots/nearby
```

**请求参数:**
- latitude: 纬度(query参数)
- longitude: 经度(query参数)
- radius: 搜索半径,单位:米(query参数,可选,默认2000)
- page: 页码(query参数,可选,默认1)
- size: 每页数量(query参数,可选,默认10)

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "total": 100,
        "pages": 10,
        "current": 1,
        "records": [
            {
                "id": 123,
                "name": "小区A-B1-123",
                "address": "上海市浦东新区xx路xx号",
                "latitude": 31.123456,
                "longitude": 121.123456,
                "price": 1000,
                "distance": 500,  // 距离,单位:米
                "images": [
                    "/upload/2024/03/21/image1.jpg"
                ],
                "rating": 4.5,
                "ratingCount": 10,
                "availableStartTime": "08:00",
                "availableEndTime": "22:00"
            }
        ]
    }
}
```

**测试命令:**
```bash
curl -X GET 'http://localhost:8080/api/parking/spots/nearby?latitude=31.123456&longitude=121.123456&radius=2000&page=1&size=10' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...'
```

### 3. 获取停车位详情
```http
GET /api/parking/spot/{id}
```

**路径参数:**
- id: 停车位ID

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "id": 123,
        "name": "小区A-B1-123",
        "address": "上海市浦东新区xx路xx号",
        "latitude": 31.123456,
        "longitude": 121.123456,
        "price": 1000,
        "images": [
            "/upload/2024/03/21/image1.jpg",
            "/upload/2024/03/21/image2.jpg"
        ],
        "description": "地下停车场B1层123号",
        "rating": 4.5,
        "ratingCount": 10,
        "owner": {
            "id": 456,
            "nickName": "车位主",
            "avatarUrl": "https://example.com/avatar.jpg",
            "phone": "138****8000"
        },
        "availableStartTime": "08:00",
        "availableEndTime": "22:00"
    }
}
```

**测试命令:**
```bash
curl -X GET 'http://localhost:8080/api/parking/spot/123' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...'
```

## 订单接口
Base URL: `/api/order`

### 1. 创建订单
```http
POST /api/order
```

**请求参数:**
```json
{
    "parkingSpotId": 123,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00"
}
```

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211000123",
        "amount": 2000,  // 总金额,单位:分
        "payUrl": "https://example.com/pay/123"  // 支付链接
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/order' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...' \
-H 'Content-Type: application/json' \
-d '{
    "parkingSpotId": 123,
    "startTime": "2024-03-21 10:00:00",
    "endTime": "2024-03-21 12:00:00"
}'
```

### 2. 获取订单列表
```http
GET /api/order/list
```

**请求参数:**
- status: 订单状态(query参数,可选)
    - 1: 待支付
    - 2: 已支付
    - 3: 已完成
    - 4: 已取消
- page: 页码(query参数,可选,默认1)
- size: 每页数量(query参数,可选,默认10)

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "total": 100,
        "pages": 10,
        "current": 1,
        "records": [
            {
                "orderId": "202403211000123",
                "parkingSpotName": "小区A-B1-123",
                "startTime": "2024-03-21 10:00:00",
                "endTime": "2024-03-21 12:00:00",
                "amount": 2000,
                "status": 2,
                "createTime": "2024-03-21 09:50:00"
            }
        ]
    }
}
```

**测试命令:**
```bash
curl -X GET 'http://localhost:8080/api/order/list?status=2&page=1&size=10' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...'
```

### 3. 获取订单详情
```http
GET /api/order/{orderId}
```

**路径参数:**
- orderId: 订单ID

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "orderId": "202403211000123",
        "parkingSpot": {
            "id": 123,
            "name": "小区A-B1-123",
            "address": "上海市浦东新区xx路xx号",
            "images": ["/upload/2024/03/21/image1.jpg"]
        },
        "startTime": "2024-03-21 10:00:00",
        "endTime": "2024-03-21 12:00:00",
        "amount": 2000,
        "status": 2,
        "createTime": "2024-03-21 09:50:00",
        "payTime": "2024-03-21 09:55:00",
        "owner": {
            "id": 456,
            "nickName": "车位主",
            "avatarUrl": "https://example.com/avatar.jpg",
            "phone": "138****8000"
        }
    }
}
```

**测试命令:**
```bash
curl -X GET 'http://localhost:8080/api/order/202403211000123' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...'
```

### 4. 取消订单
```http
POST /api/order/{orderId}/cancel
```

**路径参数:**
- orderId: 订单ID

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "message": "order cancelled successfully"
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/order/202403211000123/cancel' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...'
```

### 5. 完成订单
```http
POST /api/order/{orderId}/complete
```

**路径参数:**
- orderId: 订单ID

**请求参数:**
```json
{
    "rating": 5,  // 评分 1-5
    "comment": "车位很好找,环境也不错"  // 评价内容
}
```

**响应示例:**
```json
{
    "code": 200,
    "data": {
        "message": "order completed successfully"
    }
}
```

**测试命令:**
```bash
curl -X POST 'http://localhost:8080/api/order/202403211000123/complete' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1...' \
-H 'Content-Type: application/json' \
-d '{
    "rating": 5,
    "comment": "车位很好找,环境也不错"
}'
```

## 订单状态说明

| 状态码 | 说明 |
|--------|------|
| 1 | 待支付 |
| 2 | 已支付 |
| 3 | 已完成 |
| 4 | 已取消 |

## 注意事项

1. 停车位相关:
- 价格单位为分
- 图片最多上传5张
- 经纬度保留6位小数
- 搜索半径最大5000米

2. 订单相关:
- 订单号格式:年月日时分+3位随机数
- 订单创建后15分钟内未支付自动取消
- 已支付订单不可取消
- 订单完成后才能评价
- 评分范围1-5

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 环境信息

- 开发环境: http://localhost:8080
- 测试环境: http://test-api.example.com
- 生产环境: https://api.example.com 