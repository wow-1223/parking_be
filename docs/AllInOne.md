# hello

| Version | Update Time | Status | Author | Description |
|---------|-------------|--------|--------|-------------|
|v2025-03-04 17:56:29|2025-03-04 17:56:29|auto|@tadowli|Created by smart-doc|



# 公共模块
## 
### 
**URL:** /api/upload/file

**Type:** POST


**Content-Type:** multipart/form-data

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|file|file|true|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: multipart/form-data' -F 'file=' -i '/api/upload/file'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|No comments found.|-|
|message|string|No comments found.|-|
|data|string|No comments found.|-|

**Response-example:**
```json
{
  "success": true,
  "message": "",
  "data": ""
}
```

### 
**URL:** /api/upload/files

**Type:** POST


**Content-Type:** multipart/form-data

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|files|file|true|No comments found.(array of file)|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: multipart/form-data' -F 'files=' -i '/api/upload/files'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|No comments found.|-|
|message|string|No comments found.|-|
|data|array|No comments found.|-|

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

### 
**URL:** /api/upload

**Type:** DELETE


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|fileUrl|string|true|No comments found.|-|

**Request-example:**
```bash
curl -X DELETE -i '/api/upload?fileUrl='
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|success|boolean|No comments found.|-|
|message|string|No comments found.|-|
|data|object|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
## 
### 
**URL:** /api/owner/parking/createParking

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|location|string|false|No comments found.|-|
|latitude|double|false|No comments found.|-|
|longitude|double|false|No comments found.|-|
|description|string|false|No comments found.|-|
|price|number|false|No comments found.|-|
|images|array|false|No comments found.|-|
|rules|array|false|No comments found.|-|
|└─mode|string|false|No comments found.|-|
|└─startTime|string|false|No comments found.|-|
|└─endTime|string|false|No comments found.|-|
|└─specificDates|array|false|No comments found.|-|
|└─specificWeekDays|array|false|No comments found.|-|
|└─specificMonthDateRanges|array|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startDay|int32|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endDay|int32|false|No comments found.|-|
|facilities|array|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
**URL:** /api/owner/parking/updateParking

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|location|string|false|No comments found.|-|
|latitude|double|false|No comments found.|-|
|longitude|double|false|No comments found.|-|
|description|string|false|No comments found.|-|
|price|number|false|No comments found.|-|
|images|array|false|No comments found.|-|
|rules|array|false|No comments found.|-|
|└─mode|string|false|No comments found.|-|
|└─startTime|string|false|No comments found.|-|
|└─endTime|string|false|No comments found.|-|
|└─specificDates|array|false|No comments found.|-|
|└─specificWeekDays|array|false|No comments found.|-|
|└─specificMonthDateRanges|array|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─startDay|int32|false|No comments found.|-|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─endDay|int32|false|No comments found.|-|
|facilities|array|false|No comments found.|-|
|id|int64|false|No comments found.|-|
|status|int32|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
**URL:** /api/owner/parking/deleteParking

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
**URL:** /api/owner/parking/getParkingList

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|status|int32|false|No comments found.|-|
|page|int32|false|No comments found.|-|
|size|int32|false|No comments found.|-|

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

### 
**URL:** /api/owner/parking/getParkingDetail

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|true|No comments found.|-|

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

## 
### 
**URL:** /api/owner/orders/getOrders

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|ownerId|int64|true|No comments found.|-|
|status|int32|false|No comments found.|-|
|page|int32|false|No comments found.|-|
|size|int32|false|No comments found.|-|

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

### 
**URL:** /api/owner/orders/earnings

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|ownerId|int64|false|No comments found.|-|
|parkingSpotId|int64|false|No comments found.|-|
|startTime|string|false|No comments found.|-|
|endTime|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/orders/earnings' --data '{
  "ownerId": 0,
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

### 
**URL:** /api/owner/orders/usage

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|ownerId|int64|false|No comments found.|-|
|parkingSpotId|int64|false|No comments found.|-|
|startTime|string|false|No comments found.|-|
|endTime|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/owner/orders/usage' --data '{
  "ownerId": 0,
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
## 
### 
**URL:** /api/user/favorites/toggleFavorite

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|id|int64|false|No comments found.|-|
|userId|int64|false|No comments found.|-|
|parkingSpotId|int64|false|No comments found.|-|
|action|boolean|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/favorites/toggleFavorite' --data '{
  "id": 0,
  "userId": 0,
  "parkingSpotId": 0,
  "action": true
}'
```
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
|code|int32|错误码|-|
|message|string|错误信息|-|
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
**URL:** /api/user/favorites/getFavorites

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|userId|int64|true|No comments found.|-|
|page|int32|false|No comments found.|-|
|size|int32|false|No comments found.|-|

**Request-example:**
```bash
curl -X GET -i '/api/user/favorites/getFavorites?userId=0&page=1&size=20' --data '&0&"1"&"20"'
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

## 
### 
**URL:** /api/user/parking/nearby

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|latitude|double|false|No comments found.|-|
|longitude|double|false|No comments found.|-|
|radius|int32|false|No comments found.|-|
|price|number|false|No comments found.|-|
|startTime|string|false|No comments found.|-|
|endTime|string|false|No comments found.|-|
|page|int32|false|No comments found.|-|
|size|int32|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -i '/api/user/parking/nearby' --data '{
  "latitude": 0.0,
  "longitude": 0.0,
  "radius": 0,
  "price": 0,
  "startTime": "",
  "endTime": "",
  "page": 0,
  "size": 0
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

## 
### 
**URL:** /api/user/test

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Request-example:**
```bash
curl -X GET -i '/api/user/test'
```

**Response-example:**
```json
string
```

### 
**URL:** /api/user/login/wechat/{code}

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|code|string|true|No comments found.|-|

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

### 
**URL:** /api/user/login/phone

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|false|No comments found.|-|
|verifyCode|string|false|No comments found.|-|
|password|string|false|No comments found.|-|

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

### 
**URL:** /api/user/sendVerifyCode

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|true|No comments found.|-|

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

### 
**URL:** /api/user/register

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|phone|string|false|No comments found.|-|
|verifyCode|string|false|No comments found.|-|
|password|string|false|No comments found.|-|

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

## 
### 
**URL:** /api/user/orders/getOrders

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|status|int32|false|No comments found.|-|
|page|int32|false|No comments found.|-|
|size|int32|false|No comments found.|-|

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

### 
**URL:** /api/user/orders/createOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|parkingSpotId|int64|false|No comments found.|-|
|startTime|string|false|No comments found.|-|
|endTime|string|false|No comments found.|-|
|carNumber|string|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
**URL:** /api/user/orders/cancelOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|int64|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
|orderId|int64|false|No comments found.|-|

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
|success|boolean|No comments found.|-|
|id|int64|No comments found.|-|

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
## 
### 
**URL:** /api/pay/createPayOrder

**Type:** POST


**Content-Type:** application/json

**Description:** 

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

### 
**URL:** /api/pay/query/{orderId}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|orderId|string|true|No comments found.|-|

**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|payType|string|true|No comments found.|-|

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

## 回调处理
 支付回调：1、解析参数 2、验证签名 3、返回成功或失败 4、异步处理业务逻辑（包含重试机制）
### 
**URL:** /api/pay/notify/alipay

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Request-example:**
```bash
curl -X POST -i '/api/pay/notify/alipay'
```

**Response-example:**
```json
string
```

### 
**URL:** /api/pay/notify/wechatpay

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|Wechatpay-Signature|string|true|No comments found.|-|
|Wechatpay-Nonce|string|true|No comments found.|-|
|Wechatpay-Timestamp|string|true|No comments found.|-|
|Wechatpay-Serial|string|true|No comments found.|-|


**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|notifyData|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -H 'Wechatpay-Signature' -H 'Wechatpay-Nonce' -H 'Wechatpay-Timestamp' -H 'Wechatpay-Serial' -i '/api/pay/notify/wechatpay'
```

**Response-example:**
```json
string
```

### 
**URL:** /api/pay/notify/alipay/refund

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=UTF-8

**Description:** 

**Request-example:**
```bash
curl -X POST -i '/api/pay/notify/alipay/refund'
```

**Response-example:**
```json
string
```

### 
**URL:** /api/pay/notify/wechatpay/refund

**Type:** POST


**Content-Type:** application/json

**Description:** 

**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
|Wechatpay-Signature|string|true|No comments found.|-|
|Wechatpay-Nonce|string|true|No comments found.|-|
|Wechatpay-Timestamp|string|true|No comments found.|-|
|Wechatpay-Serial|string|true|No comments found.|-|


**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
|notifyData|string|false|No comments found.|-|

**Request-example:**
```bash
curl -X POST -H 'Content-Type: application/json' -H 'Wechatpay-Signature' -H 'Wechatpay-Nonce' -H 'Wechatpay-Timestamp' -H 'Wechatpay-Serial' -i '/api/pay/notify/wechatpay/refund'
```

**Response-example:**
```json
string
```


