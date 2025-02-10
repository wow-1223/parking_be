# 共享停车位小程序后台服务

## 项目结构 

## 功能模块说明

### 1. 用户模块 (com.parking.controller.common)
- 用户注册
- 用户登录
- 身份验证
- 用户信息管理

### 2. 停车位管理模块 (com.parking.controller.user)
#### 用户端
- 查询附近停车位
- 获取停车位详情
- 预订停车位
- 停车位筛选

#### 出租方 (com.parking.controller.owner)
- 发布停车位信息
- 管理停车位信息
- 更新停车位状态

### 3. 订单管理模块
#### 用户端 (com.parking.controller.user)
- 创建订单
- 查询订单列表
- 取消订单
- 订单支付

#### 出租方 (com.parking.controller.owner)
- 订单确认/拒绝
- 查看订单列表
- 订单统计

### 4. 评价模块 (com.parking.controller.user)
- 提交评价
- 查看评价
- 评分管理

### 5. 收益管理模块 (com.parking.controller.owner)
- 收益统计
- 收益明细
- 收益报表

## API文档

### 用户端接口

#### 1. 用户认证
##### 1.1 微信登录
- 请求路径：POST /api/user/login
- 接口描述：使用微信code进行登录认证
- 请求参数：
  ```json
  {
    "code": "string"  // 微信登录临时凭证code，必填
  }
  ```
- 响应结果：
  ```json
  {
    "token": "string",     // JWT认证令牌
    "userId": "string",    // 用户ID
    "userInfo": {          // 用户信息
      "nickName": "string",  // 用户昵称
      "avatarUrl": "string"  // 用户头像URL
    }
  }
  ```
- 响应状态：
    - 200：登录成功
    - 400：参数错误
    - 500：服务器错误

#### 2. 停车位管理
##### 2.1 获取附近停车位
- 请求路径：GET /api/parking/nearby
- 请求参数：
  ```
  latitude    float    纬度，必填
  longitude   float    经度，必填
  radius      int      搜索半径(米)，选填，默认1000
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：
  ```json
  {
    "total": 100,           // 总记录数
    "pages": 5,             // 总页数
    "list": [{
      "id": "string",       // 停车位ID
      "location": "string", // 位置描述
      "latitude": 0.0,      // 纬度
      "longitude": 0.0,     // 经度
      "price": 0.0,        // 每小时价格
      "distance": 0.0,     // 距离(米)
      "status": "string"   // 状态：AVAILABLE/OCCUPIED
    }]
  }
  ```

##### 2.2 搜索停车位
- 请求路径：GET /api/parking/search
- 请求参数：
  ```
  keyword     string   搜索关键词，必填
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：同获取附近停车位

##### 2.3 获取停车位详情
- 请求路径：GET /api/parking/{id}
- 路径参数：
  ```
  id    string    停车位ID，必填
  ```
- 响应结果：
  ```json
  {
    "id": "string",           // 停车位ID
    "location": "string",     // 位置描述
    "latitude": 0.0,          // 纬度
    "longitude": 0.0,         // 经度
    "description": "string",  // 详细描述
    "price": 0.0,            // 每小时价格
    "images": ["string"],     // 图片URL列表
    "rules": {               // 使用规则
      "openTime": "string",   // 开放时间
      "restrictions": ["string"] // 限制说明
    },
    "facilities": ["string"], // 设施列表
    "status": "string",      // 状态
    "owner": {               // 车位所有者信息
      "id": "string",
      "name": "string",
      "phone": "string"
    }
  }
  ```

#### 3. 订单管理
##### 3.1 创建订单
- 请求路径：POST /api/orders/createOrder
- 请求参数：
  ```json
  {
    "parkingSpotId": "string",  // 停车位ID，必填
    "carNumber": "string",      // 车牌号，必填
    "startTime": "string",      // 开始时间，必填，格式：yyyy-MM-dd HH:mm:ss
    "endTime": "string"         // 结束时间，必填，格式：yyyy-MM-dd HH:mm:ss
  }
  ```
- 响应结果：
  ```json
  {
    "orderId": "string",     // 订单ID
    "amount": 0.0,          // 订单金额
    "paymentId": "string"   // 支付ID
  }
  ```

#### 4. 收藏管理
##### 4.1 收藏/取消收藏
- 请求路径：POST /api/favorites/toggleFavorite
- 接口描述：收藏或取消收藏停车位
- 请求参数：
  ```json
  {
    "parkingSpotId": "string",  // 停车位ID，必填
    "action": "string"          // 操作类型：ADD/REMOVE，必填
  }
  ```
- 响应结果：
  ```json
  {
    "success": true,      // 操作是否成功
    "message": "string"   // 操作结果描述
  }
  ```

##### 4.2 获取收藏列表
- 请求路径：GET /api/favorites/getFavorites
- 请求参数：
  ```
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：
  ```json
  {
    "total": 100,           // 总记录数
    "pages": 5,             // 总页数
    "list": [{
      "id": "string",       // 停车位ID
      "location": "string", // 位置描述
      "price": 0.0,        // 每小时价格
      "status": "string",  // 状态
      "favoriteTime": "string" // 收藏时间
    }]
  }
  ```

### 出租方接口

#### 1. 车位管理
##### 1.1 发布车位
- 请求路径：POST /api/owner/parking/createParking
- 请求参数：
  ```json
  {
    "location": "string",     // 位置描述，必填
    "latitude": 0.0,          // 纬度，必填
    "longitude": 0.0,         // 经度，必填
    "description": "string",  // 详细描述，必填
    "price": 0.0,            // 每小时价格，必填
    "images": ["string"],     // 图片URL列表，选填
    "rules": {               // 使用规则，必填
      "openTime": "string",   // 开放时间
      "restrictions": ["string"] // 限制说明
    },
    "facilities": ["string"]  // 设施列表，选填
  }
  ```
- 响应结果：
  ```json
  {
    "parkingId": "string",  // 创建的停车位ID
    "success": true,        // 创建是否成功
    "message": "string"     // 结果描述
  }
  ```

##### 1.2 修改车位信息
- 请求路径：PUT /api/owner/parking/{id}
- 路径参数：
  ```
  id    string    停车位ID，必填
  ```
- 请求参数：同发布车位
- 响应结果：
  ```json
  {
    "success": true,      // 修改是否成功
    "message": "string"   // 结果描述
  }
  ```

##### 1.3 获取车位列表
- 请求路径：GET /api/owner/parking/getParkingList
- 请求参数：
  ```
  status      string   状态筛选，选填(ALL/AVAILABLE/OCCUPIED)
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：
  ```json
  {
    "total": 100,           // 总记录数
    "pages": 5,             // 总页数
    "list": [{
      "id": "string",       // 停车位ID
      "location": "string", // 位置描述
      "price": 0.0,        // 每小时价格
      "status": "string",  // 状态
      "currentOrder": {    // 当前订单信息（如果被占用）
        "orderId": "string",
        "endTime": "string"
      },
      "statistics": {      // 统计信息
        "orderCount": 0,   // 订单总数
        "income": 0.0      // 总收入
      }
    }]
  }
  ```

#### 2. 订单管理
##### 2.1 获取订单列表
- 请求路径：GET /api/owner/orders/getOrders
- 请求参数：
  ```
  status      string   订单状态，选填(ALL/PENDING/CONFIRMED/COMPLETED)
  startDate   string   开始日期，选填，格式：yyyy-MM-dd
  endDate     string   结束日期，选填，格式：yyyy-MM-dd
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：
  ```json
  {
    "total": 100,           // 总记录数
    "pages": 5,             // 总页数
    "list": [{
      "orderId": "string",  // 订单ID
      "parkingSpot": {      // 停车位信息
        "id": "string",
        "location": "string"
      },
      "user": {             // 用户信息
        "id": "string",
        "nickName": "string"
      },
      "carNumber": "string", // 车牌号
      "startTime": "string", // 开始时间
      "endTime": "string",   // 结束时间
      "amount": 0.0,        // 订单金额
      "status": "string",   // 订单状态
      "createTime": "string" // 创建时间
    }]
  }
  ```

### 管理端接口

#### 1. 收藏管理
##### 1.1 获取收藏统计
- 请求路径：GET /api/admin/favorites/stats
- 接口描述：获取车位收藏统计信息
- 请求参数：
  ```
  timeRange   string   统计时间范围，选填(day/week/month/year)
  startDate   string   开始日期，选填，格式：yyyy-MM-dd
  endDate     string   结束日期，选填，格式：yyyy-MM-dd
  ```
- 响应结果：
  ```json
  {
    "overview": {
      "totalFavorites": 0,      // 总收藏数
      "activeFavorites": 0,     // 当前有效收藏数
      "favoriteUsers": 0        // 收藏用户数
    },
    "trends": [{                // 趋势数据
      "date": "string",         // 日期
      "newFavorites": 0,        // 新增收藏数
      "removedFavorites": 0     // 取消收藏数
    }],
    "topParkingSpots": [{       // 最受欢迎车位
      "id": "string",
      "location": "string",
      "favoriteCount": 0
    }]
  }
  ```

#### 2. 用户管理
##### 2.1 获取用户列表
- 请求路径：GET /api/admin/users
- 请求参数：
  ```
  keyword     string   搜索关键词(用户名/手机号)，选填
  role        string   用户角色(USER/OWNER/ADMIN)，选填
  status      string   用户状态(ACTIVE/DISABLED)，选填
  page        int      页码，选填，默认1
  pageSize    int      每页数量，选填，默认20
  ```
- 响应结果：
  ```json
  {
    "total": 100,
    "pages": 5,
    "list": [{
      "id": "string",
      "openId": "string",
      "nickName": "string",
      "avatarUrl": "string",
      "phone": "string",
      "role": "string",
      "status": "string",
      "createTime": "string",
      "lastLoginTime": "string",
      "statistics": {
        "orderCount": 0,        // 订单总数
        "favoriteCount": 0,     // 收藏数量
        "parkingSpotCount": 0   // 发布车位数(车主)
      }
    }]
  }
  ```

##### 2.2 修改用户状态
- 请求路径：PUT /api/admin/users/{id}/status
- 路径参数：
  ```
  id    string    用户ID，必填
  ```
- 请求参数：
  ```json
  {
    "status": "string",    // 新状态：ACTIVE/DISABLED，必填
    "reason": "string"     // 修改原因，选填
  }
  ```
- 响应结果：
  ```json
  {
    "success": true,
    "message": "string"
  }
  ```

#### 3. 统计分析
##### 3.1 获取平台概况
- 请求路径：GET /api/admin/statistics/overview
- 请求参数：
  ```
  timeRange   string   统计时间范围，选填(day/week/month/year)
  ```
- 响应结果：
  ```json
  {
    "userStats": {
      "totalUsers": 0,          // 总用户数
      "activeUsers": 0,         // 活跃用户数
      "newUsers": 0            // 新增用户数
    },
    "parkingStats": {
      "totalSpots": 0,         // 总车位数
      "availableSpots": 0,     // 可用车位数
      "occupiedSpots": 0       // 已占用车位数
    },
    "orderStats": {
      "totalOrders": 0,        // 总订单数
      "completedOrders": 0,    // 完成订单数
      "totalAmount": 0.0,      // 总交易金额
      "avgOrderAmount": 0.0    // 平均订单金额
    },
    "trends": {
      "userTrend": [{          // 用户趋势
        "date": "string",
        "value": 0
      }],
      "orderTrend": [{         // 订单趋势
        "date": "string",
        "value": 0
      }],
      "revenueTrend": [{       // 收入趋势
        "date": "string",
        "value": 0.0
      }]
    }
  }
  ```

##### 3.2 导出统计报表
- 请求路径：GET /api/admin/statistics/export
- 请求参数：
  ```
  type        string   报表类型(USER/ORDER/REVENUE)，必填
  timeRange   string   统计时间范围，必填
  startDate   string   开始日期，选填
  endDate     string   结束日期，选填
  format      string   导出格式(CSV/EXCEL)，选填，默认EXCEL
  ```
- 响应结果：
    - 文件流，根据format参数返回对应格式的报表文件
## 技术栈
- Spring Boot 2.7.x
- Spring Security
- MySQL 8.0
- Redis
- MyBatis-Plus
- JWT

## 配置说明
- application.properties: 通用配置
- application-dev.properties: 开发环境配置
- application-prod.properties: 生产环境配置

## 开发环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## DB设计

### 用户表(users)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| open_id | VARCHAR(64) | 微信openid |
| nick_name | VARCHAR(64) | 昵称 |
| avatar_url | VARCHAR(255) | 头像 |
| phone | VARCHAR(20) | 手机号 |
| role | VARCHAR(20) | 角色: USER/OWNER/ADMIN |
| status | VARCHAR(20) | 状态: ACTIVE/DISABLED |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 停车位表(parking_spots)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| owner_id | BIGINT | 车位所有者ID |
| location | VARCHAR(255) | 位置描述 |
| latitude | DOUBLE | 纬度 |
| longitude | DOUBLE | 经度 |
| description | TEXT | 详细描述 |
| price | DECIMAL(10,2) | 每小时价格 |
| images | JSON | 图片列表 |
| rules | JSON | 使用规则 |
| facilities | JSON | 设施列表 |
| status | VARCHAR(20) | 状态 |
| current_order_id | BIGINT | 当前订单ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 订单表(orders)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| parking_spot_id | BIGINT | 停车位ID |
| user_id | BIGINT | 用户ID |
| car_number | VARCHAR(20) | 车牌号 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| amount | DECIMAL(10,2) | 订单金额 |
| status | VARCHAR(20) | 状态 |
| payment_id | VARCHAR(64) | 支付ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 收藏表(favorites)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| parking_spot_id | BIGINT | 停车位ID |
| create_time | DATETIME | 创建时间 |

### 评价表(reviews)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单ID |
| user_id | BIGINT | 用户ID |
| parking_spot_id | BIGINT | 停车位ID |
| rating | TINYINT | 评分: 1-5 |
| content | TEXT | 评价内容 |
| images | JSON | 图片列表 |
| create_time | DATETIME | 创建时间 |

### 支付记录表(payments)
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单ID |
| transaction_id | VARCHAR(64) | 微信支付交易号 |
| amount | DECIMAL(10,2) | 支付金额 |
| status | VARCHAR(20) | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

DB
