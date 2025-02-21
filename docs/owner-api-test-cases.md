# API 接口文档及测试用例

## 1. 租户停车位接口 (/api/owner/parking)

### 2.1 获取附近停车位
- 接口: POST /api/owner/parking/nearby
- 描述: 获取租户附近的可用停车位
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
curl -X POST 'http://localhost:8080/api/owner/parking/nearby' \
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
- 接口: POST /api/owner/parking/detail
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
      "name": "租户831",
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
curl -X POST 'http://localhost:8080/api/owner/parking/detail' \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
        "id": 920,
        "startTime": "2024-03-21 10:00:00",
        "endTime": "2024-03-21 13:00:00"
    }'
```

## 3. 租户订单接口 (/api/owner/orders)

### 3.1 获取订单列表
- 接口: POST /api/owner/orders/getOrders
- 描述: 获取租户订单列表
- 请求参数:
```json
{
    "ownerId": 1,
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
      "ownerId": 1,
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
      "ownerId": 1,
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
curl -X GET 'http://localhost:8080/api/owner/orders/getOrders?ownerId=1&page=1&size=20' \
  -H 'Authorization: Bearer your_token_here'
```

### 3.2 创建订单
- 接口: POST /api/owner/orders/createOrder
- 描述: 创建订单
- 请求参数:
```json
{
  "ownerId": 2,
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
curl -X POST http://localhost:8080/api/owner/orders/createOrder \
  -H 'Content-Type: application/json' \
  -d '{
        "ownerId": 2,
        "parkingSpotId": 822,
        "startTime": "2024-03-21 10:00:00",
        "endTime": "2024-03-21 12:00:00",
        "carNumber": "沪A12346",
        "amount": 20.00
    }'
```

### 3.3 取消订单
- 接口: POST /api/owner/orders/cancelOrder
- 描述: 取消订单
- 请求参数:
```json
{
  "ownerId": 2,
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
curl -X POST http://localhost:8080/api/owner/orders/cancelOrder \
  -H 'Content-Type: application/json' \
  -d '{
      "ownerId": 2,
      "orderId": 1892934248510853122
    }'
```