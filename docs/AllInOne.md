# hello

| Version | Update Time | Status | Author | Description |
|---------|-------------|--------|--------|-------------|
|v2025-03-05 15:28:07|2025-03-05 15:28:07|auto|@tadowli|Created by smart-doc|



# 公共模块
## 文件上传
### 文件上传
**URL:** /api/upload/file

**Type:** POST


**Content-Type:** multipart/form-data

**Description:** 文件上传

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|file|file|true|文件|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: multipart/form-data' -F 'file=' -i '/api/upload/file'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|是否成功|-|
|message|string|信息|-|
|data|string|数据|-|

**Response-example:**
```json
{
  "success": true,
  "message": "",
  "data": ""
}
```

### 文件批量上传
**URL:** /api/upload/files

**Type:** POST


**Content-Type:** multipart/form-data

**Description:** 文件批量上传

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|files|file|true|文件列表(array of file)|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: multipart/form-data' -F 'files=' -i '/api/upload/files'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|是否成功|-|
|message|string|信息|-|
|data|array|数据|-|

**Response-example:**
```json
{
  "success": true,
  "message": "",
  "data": [
    "",
    ""
  ]
}
```

### 文件删除
**URL:** /api/upload

**Type:** DELETE


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 文件删除

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|fileUrl|string|true|文件地址|-|

**Request-example:**
```bash
curl -X DELETE -i '/api/upload?fileUrl='
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|是否成功|-|
|message|string|信息|-|
|data|object|数据|-|

**Response-example:**
```json
{
  "success": true,
  "message": ""
}
```

# 地锁模块
## 
### 
**URL:** /api/lock/bind

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|AUTHORIZATION|string|true|No comments found.|-|


**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|deviceId|string|false|No comments found.|-|
|parkingSpotId|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -H 'AUTHORIZATION' -i '/api/lock/bind' --data '{
  "deviceId": "",
  "parkingSpotId": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 
**URL:** /api/lock/status

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|AUTHORIZATION|string|true|No comments found.|-|


**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|deviceId|string|false|No comments found.|-|
|status|string|false|No comments found.|-|
|timestamp|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -H 'AUTHORIZATION' -i '/api/lock/status' --data '{
  "deviceId": "",
  "status": "",
  "timestamp": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 
**URL:** /api/lock/control

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|AUTHORIZATION|string|true|No comments found.|-|


**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|deviceId|string|false|No comments found.|-|
|command|string|false|No comments found.|-|
|operator|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -H 'AUTHORIZATION' -i '/api/lock/control' --data '{
  "deviceId": "",
  "command": "",
  "operator": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 
**URL:** /api/lock/{deviceId}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|AUTHORIZATION|string|true|No comments found.|-|


**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|deviceId|string|true|No comments found.|-|

**Request-example:**
```bash
curl -X GET -H 'AUTHORIZATION' -i '/api/lock/{deviceId}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|data|object|No comments found.|-|
|└─deviceId|string|No comments found.|-|
|└─status|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "data": {
    "deviceId": "",
    "status": ""
  }
}
```

# 租户模块
## 租户停车位相关接口
### 创建停车位
**URL:** /api/owner/parking/createParking

**Type:** POST


**Content-Type:** application/json

**Description:** 创建停车位

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|location|string|false|停车场名称|-|
|latitude|double|false|纬度|-|
|longitude|double|false|经度|-|
|description|string|false|车位描述|-|
|price|number|false|车位价格|-|
|images|array|false|车位图片|-|
|rules|array|false|车位规则|-|
|└─mode|string|false|车位出租模式|-|
|└─startTime|string|false|开始时间|-|
|└─endTime|string|false|结束时间|-|
|└─specificDates|array|false|No comments found.|-|
|└─specificWeekDays|array|false|No comments found.|-|
|└─specificMonthDateRanges|array|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startDay|int32|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endDay|int32|false|No comments found.|-|
|facilities|array|false|车位设施|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/parking/createParking' --data '{
  "location": "",
  "latitude": 0.0,
  "longitude": 0.0,
  "description": "",
  "price": 0,
  "images": [
    ""
  ],
  "rules": [
    {
      "mode": "",
      "startTime": "",
      "endTime": "",
      "specificDates": [
        ""
      ],
      "specificWeekDays": [
        ""
      ],
      "specificMonthDateRanges": [
        {
          "startDay": 0,
          "endDay": 0
        }
      ]
    }
  ],
  "facilities": [
    ""
  ]
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 更新停车位
**URL:** /api/owner/parking/updateParking

**Type:** POST


**Content-Type:** application/json

**Description:** 更新停车位

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|location|string|false|停车场名称|-|
|latitude|double|false|纬度|-|
|longitude|double|false|经度|-|
|description|string|false|车位描述|-|
|price|number|false|车位价格|-|
|images|array|false|车位图片|-|
|rules|array|false|车位规则|-|
|└─mode|string|false|车位出租模式|-|
|└─startTime|string|false|开始时间|-|
|└─endTime|string|false|结束时间|-|
|└─specificDates|array|false|No comments found.|-|
|└─specificWeekDays|array|false|No comments found.|-|
|└─specificMonthDateRanges|array|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startDay|int32|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endDay|int32|false|No comments found.|-|
|facilities|array|false|车位设施|-|
|id|int64|false|车位id|-|
|status|int32|false|车位状态|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/parking/updateParking' --data '{
  "location": "",
  "latitude": 0.0,
  "longitude": 0.0,
  "description": "",
  "price": 0,
  "images": [
    ""
  ],
  "rules": [
    {
      "mode": "",
      "startTime": "",
      "endTime": "",
      "specificDates": [
        ""
      ],
      "specificWeekDays": [
        ""
      ],
      "specificMonthDateRanges": [
        {
          "startDay": 0,
          "endDay": 0
        }
      ]
    }
  ],
  "facilities": [
    ""
  ],
  "id": 0,
  "status": 0
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 删除停车位
**URL:** /api/owner/parking/deleteParking

**Type:** POST


**Content-Type:** application/json

**Description:** 删除停车位

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|车位id|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/parking/deleteParking' --data '{
  "parkingSpotId": 0
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 获取停车位列表
**URL:** /api/owner/parking/getParkingList

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 获取停车位列表

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|status|int32|false|停车位状态，可选，不传则查询全部|-|
|page|int32|false|页码，默认1|-|
|size|int32|false|每页数量，默认20|-|

**Request-example:**
```bash
curl -X GET -i '/api/owner/parking/getParkingList?status=0&page=1&size=20' --data '&0&"1"&"20"'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|total|int64|No comments found.|-|
|list|array|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─location|string|No comments found.|-|
|└─price|number|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "total": 0,
  "list": [
    {
      "id": 0,
      "latitude": 0.0,
      "longitude": 0.0,
      "location": "",
      "price": 0
    }
  ]
}
```

### 获取停车位详情
**URL:** /api/owner/parking/getParkingDetail

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 获取停车位详情

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|true|停车位id|-|

**Request-example:**
```bash
curl -X GET -i '/api/owner/parking/getParkingDetail?parkingSpotId=0' --data '&0'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|data|object|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─location|string|No comments found.|-|
|└─price|number|No comments found.|-|
|└─description|string|No comments found.|-|
|└─images|array|No comments found.|-|
|└─facilities|array|No comments found.|-|
|└─owner|object|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|int64|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|No comments found.|-|
|└─parkingIntervals|array|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startTime|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endTime|string|No comments found.|-|
|└─occupiedIntervals|array|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startTime|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endTime|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "data": {
    "id": 0,
    "latitude": 0.0,
    "longitude": 0.0,
    "location": "",
    "price": 0,
    "description": "",
    "images": [
      ""
    ],
    "facilities": [
      ""
    ],
    "owner": {
      "id": 0,
      "name": "",
      "phone": ""
    },
    "parkingIntervals": [
      {
        "startTime": "HH:mm:ss",
        "endTime": "HH:mm:ss"
      }
    ],
    "occupiedIntervals": [
      {
        "startTime": "HH:mm:ss",
        "endTime": "HH:mm:ss"
      }
    ]
  }
}
```

## 租户订单
### 获取订单列表
**URL:** /api/owner/orders/getOrders

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 获取订单列表

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|ownerId|int64|true|租户id|-|
|status|int32|false| 订单状态|-|
|page|int32|false|   页码|-|
|size|int32|false|   每页数量|-|

**Request-example:**
```bash
curl -X GET -i '/api/owner/orders/getOrders?ownerId=0&status=0&page=1&size=20' --data '&0&0&"1"&"20"'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|total|int64|No comments found.|-|
|list|array|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─userId|int64|No comments found.|-|
|└─parkingSpotId|int64|No comments found.|-|
|└─ownerId|int64|No comments found.|-|
|└─location|string|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─occupiedSpotId|int64|No comments found.|-|
|└─startTime|string|No comments found.|-|
|└─endTime|string|No comments found.|-|
|└─carNumber|string|No comments found.|-|
|└─amount|number|No comments found.|-|
|└─refundAmount|number|No comments found.|-|
|└─transactionId|string|No comments found.|-|
|└─status|int32|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "total": 0,
  "list": [
    {
      "id": 0,
      "userId": 0,
      "parkingSpotId": 0,
      "ownerId": 0,
      "location": "",
      "longitude": 0.0,
      "latitude": 0.0,
      "occupiedSpotId": 0,
      "startTime": "yyyy-MM-dd HH:mm:ss",
      "endTime": "yyyy-MM-dd HH:mm:ss",
      "carNumber": "",
      "amount": 0,
      "refundAmount": 0,
      "transactionId": "",
      "status": 0
    }
  ]
}
```

### 获取订单详情
**URL:** /api/owner/orders/earnings

**Type:** POST


**Content-Type:** application/json

**Description:** 获取订单详情

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|车位id|-|
|startTime|string|false|开始时间|-|
|endTime|string|false|结束时间|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/orders/earnings' --data '{
  "parkingSpotId": 0,
  "startTime": "",
  "endTime": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|totalAmount|number|总收入|-|
|totalOrders|int64|总订单数|-|
|averageAmount|number|日均收入|-|
|averageOrders|double|日均订单数|-|
|growthRate|double|环比增长率(%)|-|
|dailyStatistics|array|每日统计数据|-|
|└─date|string|日期，格式：yyyy-MM-dd|-|
|└─orderCount|int64|订单数量|-|
|└─amount|number|收入金额|-|
|└─usageRate|double|使用率(%)|-|
|└─completionRate|double|完成率(%)|-|
|└─cancellationRate|double|取消率(%)|-|
|└─averageUsageHours|double|平均使用时长(小时)|-|
|startDate|string|统计开始日期|-|
|endDate|string|统计结束日期|-|
|timeRange|string|统计时间范围<br/>day/week/month/custom|-|

**Response-example:**
```json
{
  "totalAmount": 0,
  "totalOrders": 0,
  "averageAmount": 0,
  "averageOrders": 0.0,
  "growthRate": 0.0,
  "dailyStatistics": [
    {
      "date": "",
      "orderCount": 0,
      "amount": 0,
      "usageRate": 0.0,
      "completionRate": 0.0,
      "cancellationRate": 0.0,
      "averageUsageHours": 0.0
    }
  ],
  "startDate": "",
  "endDate": "",
  "timeRange": ""
}
```

### 获取使用统计
**URL:** /api/owner/orders/usage

**Type:** POST


**Content-Type:** application/json

**Description:** 获取使用统计

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|车位id|-|
|startTime|string|false|开始时间|-|
|endTime|string|false|结束时间|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/orders/usage' --data '{
  "parkingSpotId": 0,
  "startTime": "",
  "endTime": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|totalOrders|int64|总订单数|-|
|completedOrders|int64|已完成订单数|-|
|cancelledOrders|int64|已取消订单数|-|
|totalAmount|number|总收入|-|
|averageUsageRate|double|平均使用率(%)|-|
|completionRate|double|完成率(%)|-|
|cancellationRate|double|取消率(%)|-|
|averageUsageHours|double|平均使用时长(小时)|-|
|dailyStatistics|array|每日统计数据|-|
|└─date|string|日期，格式：yyyy-MM-dd|-|
|└─orderCount|int64|订单数量|-|
|└─amount|number|收入金额|-|
|└─usageRate|double|使用率(%)|-|
|└─completionRate|double|完成率(%)|-|
|└─cancellationRate|double|取消率(%)|-|
|└─averageUsageHours|double|平均使用时长(小时)|-|
|startDate|string|统计开始日期|-|
|endDate|string|统计结束日期|-|
|timeRange|string|统计时间范围<br/>day/week/month/custom|-|
|parkingSpotId|string|停车位ID<br/>如果为null则表示所有停车位的统计|-|

**Response-example:**
```json
{
  "totalOrders": 0,
  "completedOrders": 0,
  "cancelledOrders": 0,
  "totalAmount": 0,
  "averageUsageRate": 0.0,
  "completionRate": 0.0,
  "cancellationRate": 0.0,
  "averageUsageHours": 0.0,
  "dailyStatistics": [
    {
      "date": "",
      "orderCount": 0,
      "amount": 0,
      "usageRate": 0.0,
      "completionRate": 0.0,
      "cancellationRate": 0.0,
      "averageUsageHours": 0.0
    }
  ],
  "startDate": "",
  "endDate": "",
  "timeRange": "",
  "parkingSpotId": ""
}
```

# 用户模块
## 用户收藏相关接口
### 收藏/取消收藏
**URL:** /api/user/favorites/toggleFavorite

**Type:** POST


**Content-Type:** application/json

**Description:** 收藏/取消收藏

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int64|false|收藏id|-|
|parkingSpotId|int64|false|收藏车位id|-|
|action|boolean|false|收藏车位动作 true-收藏 false-取消收藏|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/favorites/toggleFavorite' --data '{
  "id": 0,
  "parkingSpotId": 0,
  "action": true
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 获取用户收藏列表
**URL:** /api/user/favorites/getFavorites

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 获取用户收藏列表

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|page|int32|false|页码|-|
|size|int32|false|每页数量|-|

**Request-example:**
```bash
curl -X GET -i '/api/user/favorites/getFavorites?page=1&size=20' --data '&"1"&"20"'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|total|int64|No comments found.|-|
|list|array|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─location|string|No comments found.|-|
|└─price|number|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "total": 0,
  "list": [
    {
      "id": 0,
      "latitude": 0.0,
      "longitude": 0.0,
      "location": "",
      "price": 0
    }
  ]
}
```

## 用户查询车位相关接口
### 
**URL:** /api/user/parking/nearby

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|latitude|double|false|经纬度|-|
|longitude|double|false|经纬度|-|
|radius|int32|false|半径|-|
|maxPrice|number|false|最高价格|-|
|minPrice|number|false|最低价格|-|
|parkingType|string|false|停车场类型|-|
|startTime|string|false|开始时间|-|
|endTime|string|false|结束时间|-|
|page|int32|false|页数|-|
|size|int32|false|每页大小|-|
|sortType|string|false|排序类型|-|
|sortOrder|string|false|排序方式|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/parking/nearby' --data '{
  "latitude": 0.0,
  "longitude": 0.0,
  "radius": 0,
  "maxPrice": 0,
  "minPrice": 0,
  "parkingType": "",
  "startTime": "",
  "endTime": "",
  "page": 0,
  "size": 0,
  "sortType": "",
  "sortOrder": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|total|int64|No comments found.|-|
|list|array|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─location|string|No comments found.|-|
|└─price|number|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "total": 0,
  "list": [
    {
      "id": 0,
      "latitude": 0.0,
      "longitude": 0.0,
      "location": "",
      "price": 0
    }
  ]
}
```

### 
**URL:** /api/user/parking/detail

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int64|false|No comments found.|-|
|startTime|string|false|No comments found.|-|
|endTime|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/parking/detail' --data '{
  "id": 0,
  "startTime": "",
  "endTime": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|data|object|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─location|string|No comments found.|-|
|└─price|number|No comments found.|-|
|└─description|string|No comments found.|-|
|└─images|array|No comments found.|-|
|└─facilities|array|No comments found.|-|
|└─owner|object|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|int64|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|No comments found.|-|
|└─parkingIntervals|array|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startTime|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endTime|string|No comments found.|-|
|└─occupiedIntervals|array|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startTime|string|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endTime|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "data": {
    "id": 0,
    "latitude": 0.0,
    "longitude": 0.0,
    "location": "",
    "price": 0,
    "description": "",
    "images": [
      ""
    ],
    "facilities": [
      ""
    ],
    "owner": {
      "id": 0,
      "name": "",
      "phone": ""
    },
    "parkingIntervals": [
      {
        "startTime": "HH:mm:ss",
        "endTime": "HH:mm:ss"
      }
    ],
    "occupiedIntervals": [
      {
        "startTime": "HH:mm:ss",
        "endTime": "HH:mm:ss"
      }
    ]
  }
}
```

## 用户登陆注册相关接口
### 测试接口，返回字符串&lt;br&gt;
**URL:** /api/user/test

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 测试接口，返回字符串<br>

**Request-example:**
```bash
curl -X GET -i '/api/user/test'
```

**Response-example:**
```json
string
```

### 微信登录接口
**URL:** /api/user/login/wechat/{code}

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 微信登录接口

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|code|string|true|微信code，用于获取openid和session_key，用于登录|-|

**Request-example:**
```bash
curl -X POST -i '/api/user/login/wechat/{code}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|No comments found.|-|
|data|object|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─name|string|No comments found.|-|
|└─token|string|No comments found.|-|
|└─userId|int64|No comments found.|-|
|└─phone|string|No comments found.|-|
|└─nickName|string|No comments found.|-|
|└─avatarUrl|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "data": {
    "id": 0,
    "name": "",
    "token": "",
    "userId": 0,
    "phone": "",
    "nickName": "",
    "avatarUrl": ""
  }
}
```

### 手机号登录接口
**URL:** /api/user/login/phone

**Type:** POST


**Content-Type:** application/json

**Description:** 手机号登录接口

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|false|手机号|-|
|verifyCode|string|false|验证码|-|
|password|string|false|密码|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/login/phone' --data '{
  "phone": "",
  "verifyCode": "",
  "password": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|No comments found.|-|
|data|object|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─name|string|No comments found.|-|
|└─token|string|No comments found.|-|
|└─userId|int64|No comments found.|-|
|└─phone|string|No comments found.|-|
|└─nickName|string|No comments found.|-|
|└─avatarUrl|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "data": {
    "id": 0,
    "name": "",
    "token": "",
    "userId": 0,
    "phone": "",
    "nickName": "",
    "avatarUrl": ""
  }
}
```

### 发送验证码接口
**URL:** /api/user/sendVerifyCode

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 发送验证码接口

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|true|手机号|-|

**Request-example:**
```bash
curl -X POST -i '/api/user/sendVerifyCode' --data 'phone='
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|No comments found.|-|
|data|string|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "data": ""
}
```

### 注册接口
**URL:** /api/user/register

**Type:** POST


**Content-Type:** application/json

**Description:** 注册接口

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|false|手机号|-|
|verifyCode|string|false|验证码|-|
|password|string|false|密码|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/register' --data '{
  "phone": "",
  "verifyCode": "",
  "password": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|No comments found.|-|
|data|int64|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "data": 0
}
```

## 用户订单相关接口
### 获取用户订单列表
**URL:** /api/user/orders/getOrders

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 获取用户订单列表

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|status|int32|false|订单状态|-|
|page|int32|false| 当前页数，默认1|-|
|size|int32|false| 每页数量，默认20|-|

**Request-example:**
```bash
curl -X GET -i '/api/user/orders/getOrders?status=0&page=1&size=20' --data '&0&"1"&"20"'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|total|int64|No comments found.|-|
|list|array|No comments found.|-|
|└─id|int64|No comments found.|-|
|└─userId|int64|No comments found.|-|
|└─parkingSpotId|int64|No comments found.|-|
|└─ownerId|int64|No comments found.|-|
|└─location|string|No comments found.|-|
|└─longitude|double|No comments found.|-|
|└─latitude|double|No comments found.|-|
|└─occupiedSpotId|int64|No comments found.|-|
|└─startTime|string|No comments found.|-|
|└─endTime|string|No comments found.|-|
|└─carNumber|string|No comments found.|-|
|└─amount|number|No comments found.|-|
|└─refundAmount|number|No comments found.|-|
|└─transactionId|string|No comments found.|-|
|└─status|int32|No comments found.|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "total": 0,
  "list": [
    {
      "id": 0,
      "userId": 0,
      "parkingSpotId": 0,
      "ownerId": 0,
      "location": "",
      "longitude": 0.0,
      "latitude": 0.0,
      "occupiedSpotId": 0,
      "startTime": "yyyy-MM-dd HH:mm:ss",
      "endTime": "yyyy-MM-dd HH:mm:ss",
      "carNumber": "",
      "amount": 0,
      "refundAmount": 0,
      "transactionId": "",
      "status": 0
    }
  ]
}
```

### 创建订单
**URL:** /api/user/orders/createOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 创建订单

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|停车场id|-|
|startTime|string|false|开始时间|-|
|endTime|string|false|结束时间|-|
|carNumber|string|false|车牌号|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/orders/createOrder' --data '{
  "parkingSpotId": 0,
  "startTime": "",
  "endTime": "",
  "carNumber": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 取消订单
**URL:** /api/user/orders/cancelOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 取消订单

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|int64|false|订单id|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/orders/cancelOrder' --data '{
  "orderId": 0
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

### 确认订单完成
**URL:** /api/user/orders/complete

**Type:** POST


**Content-Type:** application/json

**Description:** 确认订单完成

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|int64|false|订单id|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/orders/complete' --data '{
  "orderId": 0
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|是否成功|-|
|id|int64|车位id|-|

**Response-example:**
```json
{
  "code": 0,
  "message": "",
  "success": true,
  "id": 0
}
```

# 支付模块
## 支付相关接口
### 创建支付订单
**URL:** /api/pay/createPayOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 创建支付订单

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|string|false|No comments found.|-|
|amount|int64|false|No comments found.|-|
|description|string|false|No comments found.|-|
|payType|string|false|No comments found.|-|
|subject|string|false|商品描述|-|
|productCode|string|false|商品编码|-|
|openid|string|false|微信用户openid|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/pay/createPayOrder' --data '{
  "orderId": "",
  "amount": 0,
  "description": "",
  "payType": "",
  "subject": "",
  "productCode": "",
  "openid": ""
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|orderId|string|商户订单号|-|
|status|string|交易状态|-|

**Response-example:**
```json
{
  "orderId": "",
  "status": ""
}
```

### 查询支付订单状态
**URL:** /api/pay/query/{orderId}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 查询支付订单状态

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|string|true|订单号|-|

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|payType|string|true|支付类型|-|

**Request-example:**
```bash
curl -X GET -i '/api/pay/query/{orderId}?payType='
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|orderId|string|商户订单号|-|
|status|string|交易状态|-|

**Response-example:**
```json
{
  "orderId": "",
  "status": ""
}
```


