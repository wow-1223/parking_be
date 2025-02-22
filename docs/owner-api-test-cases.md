# 车位所有者接口文档及测试用例

## 1. 停车位管理接口 (/api/owner/parking)

### 1.1 创建停车位
- 接口: POST /api/owner/parking/createParking
- 描述: 创建新的停车位
- 请求参数:
```json
{
  "ownerId": 807,
  "location": "停车位详细地址",
  "longitude": 121.123456,
  "latitude": 31.123456,
  "price": 40.00,
  "images": ["https://example.com/uploads/202403/abc123.jpg", "https://example.com/uploads/202403/abc456.jpg"],
  "rules": [
    {
      "mode": "weekly",
      "startTime": "09:00:00",
      "endTime": "21:00:00",
      "weekDays": [1, 2, 3, 4, 5]
    }
  ],
  "facilities": [
    "带充电桩",
    "地面停车场"
  ],
  "description": "停车位描述"
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "create success",
  "success": true,
  "id": 1892955854868144130
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/owner/parking/createParking \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
        "ownerId": 807,
        "location": "停车位详细地址",
        "longitude": 121.123456,
        "latitude": 31.123456,
        "price": 40.00,
        "images": ["https://example.com/uploads/202403/abc123.jpg", "https://example.com/uploads/202403/abc456.jpg"],
        "rules": [
            {
                "mode": "weekly",
                "startTime": "09:00:00",
                "endTime": "21:00:00",
                "weekDays": [1, 2, 3, 4, 5]
            }
        ],
        "facilities": [
            "带充电桩",
            "地面停车场"
        ],
        "description": "停车位描述"
      }'
```

### 1.2 更新停车位
- 接口: POST /api/owner/parking/updateParking
- 描述: 更新停车位信息
- 请求参数:
```json
{
  "id": 1892955854868144130,
  "userId": 807,
  // "location": "停车位详细地址-updated",
  // "longitude": 121.123456,
  // "latitude": 31.123456,
  // "price": 35.00,
  // "rules": [
  //     {
  //         "mode": "weekly",
  //         "startTime": "08:00:00",
  //         "endTime": "22:00:00",
  //         "weekDays": [1, 2, 3, 4, 5, 6, 7]
  //     }
  // ],
  "status": 2
}
```
- 响应结果:
```json
{
  "code": "200",
  "message": "create success",
  "success": true,
  "id": 1892955854868144130
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/owner/parking/updateParking \
  -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
        "id": 1892955854868144130,
        "userId": 807,
        // "location": "停车位详细地址-updated",
        // "longitude": 121.123456,
        // "latitude": 31.123456,
        // "price": 35.00,
        // "rules": [
        //     {
        //         "mode": "weekly",
        //         "startTime": "08:00:00",
        //         "endTime": "22:00:00",
        //         "weekDays": [1, 2, 3, 4, 5, 6, 7]
        //     }
        // ],
        "status": 3
      }'
```

### 1.3 删除停车位
- 接口: POST /api/owner/parking/deleteParking/{id}
- 描述: 删除停车位
- 请求参数:
```json
{
  "userId": 818,
  "parkingSpotId": 986
}
```

- 响应结果:
```json
{
  "code": "200",
  "message": "delete success",
  "success": true,
  "id": 986
}
```

测试用例:
```bash
curl -X POST http://localhost:8080/api/owner/parking/deleteParking \
   -H 'Authorization: Bearer your_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
        "userId": 818,
        "parkingSpotId": 986
      }' 
```

### 1.4 获取停车位列表
- 接口: GET /api/owner/parking/getParkingList
- 描述: 获取车主的停车位列表
- 请求参数:
    - userId: 用户ID
    - status: 状态(可选)
    - page: 页码(默认1)
    - size: 每页数量(默认20)
- 响应结果:
```json
{
  "code": "200",
  "message": null,
  "total": 10,
  "list": [
    {
      "id": 1892955854868144130,
      "latitude": 31.123456,
      "longitude": 121.123456,
      "location": "\"停车位详细地址-updated\"",
      "price": 35.00
    },
    {
      "id": 75,
      "latitude": 31.038109,
      "longitude": 121.04222,
      "location": "上海市测试地址75",
      "price": 11.36
    },
    {
      "id": 192,
      "latitude": 31.402232,
      "longitude": 121.787486,
      "location": "上海市测试地址192",
      "price": 21.49
    },
    {
      "id": 240,
      "latitude": 31.324307,
      "longitude": 121.357184,
      "location": "上海市测试地址240",
      "price": 9.28
    },
    {
      "id": 274,
      "latitude": 31.310224,
      "longitude": 121.149726,
      "location": "上海市测试地址274",
      "price": 21.32
    },
    {
      "id": 451,
      "latitude": 31.282285,
      "longitude": 121.338936,
      "location": "上海市测试地址451",
      "price": 25.15
    },
    {
      "id": 549,
      "latitude": 31.250048,
      "longitude": 121.12736,
      "location": "上海市测试地址549",
      "price": 7.95
    },
    {
      "id": 772,
      "latitude": 31.229403,
      "longitude": 121.719674,
      "location": "上海市测试地址772",
      "price": 8.37
    },
    {
      "id": 822,
      "latitude": 31.230112,
      "longitude": 121.118062,
      "location": "上海市测试地址822",
      "price": 28.67
    },
    {
      "id": 897,
      "latitude": 31.264105,
      "longitude": 121.733318,
      "location": "上海市测试地址897",
      "price": 16.02
    }
  ]
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/owner/parking/getParkingList?userId=1&status=1&page=1&size=20' \
  -H 'Authorization: Bearer your_token_here'
```

### 1.4 获取停车位详情
- 接口: GET /api/owner/parking/getParkingDetail
- 描述: 获取车主的停车位详情
- 请求参数:
  - userId: 用户ID
  - parkingSpotId: 车位ID
- 响应结果:
```json
{
  "code": "200",
  "message": "get detail success",
  "data": {
    "id": 1892983387374317570,
    "latitude": 31.123456,
    "longitude": 121.123456,
    "location": "停车位详细地址_create",
    "price": 40.00,
    "description": "停车位描述",
    "images": [
      "https://example.com/uploads/202403/abc123.jpg",
      "https://example.com/uploads/202403/abc456.jpg"
    ],
    "facilities": [
      "带充电桩",
      "地面停车场"
    ],
    "owner": {
      "id": 807,
      "name": "用户807",
      "phone": null
    },
    "parkingIntervals": [
      {
        "startTime": "09:00:00",
        "endTime": "21:00:00"
      }
    ],
    "occupiedIntervals": null
  }
}
```
测试用例:
```bash
测试用例:
bash
curl -X GET 'URL_ADDRESScurl -X GET 'http://localhost:8080/api/owner/parking/getParkingDetail?userId=807&parkingSpotId=1892983387374317570' \
  -H 'Authorization: Bearer your_token_here'
```


## 2. 订单管理接口 (/api/owner/orders)

### 2.1 获取订单列表
- 接口: GET /api/owner/orders/getOrders
- 描述: 获取车主的订单列表
- 请求参数:
    - ownerId: 车主ID
    - status: 订单状态(可选)
    - page: 页码(默认1)
    - size: 每页数量(默认20)
- 响应结果:
```json
{
  "code": "200",
  "message": null,
  "total": 5,
  "list": [
    {
      "id": 14,
      "userId": 713,
      "parkingSpotId": 944,
      "ownerId": 801,
      "location": "上海市测试地址944",
      "longitude": 121.341927,
      "latitude": 31.353765,
      "occupiedSpotId": 14,
      "startTime": "2024-04-08T20:11:10",
      "endTime": "2024-04-08T07:37:35",
      "carNumber": "沪K83551",
      "amount": 2792.00,
      "refundAmount": 0.00,
      "transactionId": "wx7477ab3c17dfe4f7035c3ecde710",
      "status": 11
    },
    {
      "id": 685,
      "userId": 772,
      "parkingSpotId": 988,
      "ownerId": 895,
      "location": "上海市测试地址988",
      "longitude": 121.390381,
      "latitude": 31.031648,
      "occupiedSpotId": 685,
      "startTime": "2024-04-15T23:03:01",
      "endTime": "2024-04-15T08:08:47",
      "carNumber": "沪G26127",
      "amount": 768.00,
      "refundAmount": 0.00,
      "transactionId": "wx76b8629252ef7a27fdf74d49b715",
      "status": 8
    }
  ]
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/owner/orders/getOrders?ownerId=1&status=1&page=1&size=20' \
  -H 'Authorization: Bearer your_token_here'
```

### 2.2 获取收益统计
- 接口: POST /api/owner/orders/earnings
- 描述: 获取车主的收益统计
- 请求参数:
```json
{
    "ownerId": 807,
    "startTime": "2025-02-01 00:00:00",
    "endTime": "2025-02-29 23:59:59"
}
```
- 响应结果:
```json
{
  "totalAmount": 2093.00,
  "totalOrders": 1,
  "averageAmount": 2093.00,
  "averageOrders": 1.0,
  "growthRate": null,
  "dailyStatistics": [
    {
      "date": "2025-02-21",
      "orderCount": 1,
      "amount": 2093.00,
      "usageRate": null,
      "completionRate": null,
      "cancellationRate": null,
      "averageUsageHours": null
    }
  ],
  "startDate": null,
  "endDate": null,
  "timeRange": null
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/owner/orders/earnings' \
  -H 'Authorization: Bearer your_token_here'
  -H 'Content-Type: application/json' \
  -d '{
        "ownerId": 807,
        "startTime": "2025-02-01 00:00:00",
        "endTime": "2025-02-29 23:59:59"
      }'
```

### 2.3 获取使用统计
- 接口: GET /api/owner/orders/usage
- 描述: 获取停车位使用统计
- 请求参数:
```json
{
    "ownerId": 854,
    "parkingSpotId": 817,
    "startTime": "2025-02-01 00:00:00",
    "endTime": "2025-02-29 23:59:59"
}
```
- 响应结果:
```json
{
  "totalOrders": null,
  "completedOrders": null,
  "cancelledOrders": null,
  "totalAmount": null,
  "averageUsageRate": 3548.0,
  "completionRate": null,
  "cancellationRate": null,
  "averageUsageHours": null,
  "dailyStatistics": [
    {
      "date": "2025-02-21",
      "orderCount": null,
      "amount": null,
      "usageRate": 5238.0,
      "completionRate": null,
      "cancellationRate": null,
      "averageUsageHours": null
    },
    {
      "date": "2025-02-22",
      "orderCount": null,
      "amount": null,
      "usageRate": 1858.0,
      "completionRate": null,
      "cancellationRate": null,
      "averageUsageHours": null
    }
  ],
  "startDate": null,
  "endDate": null,
  "timeRange": null,
  "parkingSpotId": null
}
```

测试用例:
```bash
curl -X GET 'http://localhost:8080/api/owner/orders/usage' \
  -H 'Authorization: Bearer your_token_here'
  -H 'Content-Type: application/json' \
  -d '{
        "ownerId": 854,
        "parkingSpotId": 817,
        "startTime": "2025-02-01 00:00:00",
        "endTime": "2025-02-29 23:59:59"
      }'
```