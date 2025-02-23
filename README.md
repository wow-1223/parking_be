# 停车场管理系统接口文档

## 项目简介
这是一个停车场管理系统的后端服务，提供用户端和车位所有者端的完整功能支持，包括用户认证、停车位管理、订单管理、支付处理等功能。

## 系统架构
项目基于 Spring Boot 框架开发，采用 RESTful API 设计风格，主要包含以下模块：

- 用户认证模块
- 停车位管理模块
- 订单管理模块
- 支付处理模块
- 文件上传模块

## API 接口文档

### 1. 用户认证模块 (/api/user)
#### 1.1 用户登录与注册
- `POST /api/user/login/wechat/{code}` - 微信登录
- `POST /api/user/login/phone` - 手机号登录
- `POST /api/user/sendVerifyCode` - 发送验证码
- `POST /api/user/register` - 用户注册

### 2. 用户端功能 (/api/user)
#### 2.1 停车位查询
- `POST /api/user/parking/nearby` - 查询附近停车位
- `POST /api/user/parking/detail` - 获取停车位详情

#### 2.2 订单管理
- `GET /api/user/orders/getOrders` - 获取订单列表
- `POST /api/user/orders/createOrder` - 创建订单
- `POST /api/user/orders/cancelOrder` - 取消订单
- `POST /api/user/orders/complete` - 完成订单

#### 2.3 收藏管理
- `POST /api/user/favorites/toggleFavorite` - 切换收藏状态
- `GET /api/user/favorites/getFavorites` - 获取收藏列表

### 3. 车位所有者功能 (/api/owner)
#### 3.1 停车位管理
- `POST /api/owner/parking/createParking` - 创建停车位
- `POST /api/owner/parking/updateParking` - 更新停车位信息
- `POST /api/owner/parking/deleteParking` - 删除停车位
- `GET /api/owner/parking/getParkingList` - 获取停车位列表
- `GET /api/owner/parking/getParkingDetail` - 获取停车位详情

#### 3.2 订单统计
- `GET /api/owner/orders/getOrders` - 获取订单列表
- `POST /api/owner/orders/earnings` - 收益统计
- `POST /api/owner/orders/usage` - 使用率统计

### 4. 支付功能 (/api/pay)
#### 4.1 支付操作
- `POST /api/pay/createPayOrder` - 创建支付订单
- `GET /api/pay/query/{orderId}` - 查询支付状态
- `POST /api/pay/refund/{orderId}` - 退款操作

#### 4.2 支付回调
- `POST /api/pay/notify/alipay` - 支付宝支付回调
- `POST /api/pay/notify/wechatpay` - 微信支付回调
- `POST /api/pay/notify/alipay/refund` - 支付宝退款回调
- `POST /api/pay/notify/wechatpay/refund` - 微信退款回调

### 5. 文件上传 (/api/upload)
- `POST /api/upload/file` - 单文件上传
- `POST /api/upload/files` - 多文件上传
- `DELETE /api/upload` - 删除文件

## 技术特点
1. 采用工厂模式处理不同支付方式
2. 使用异步处理支付回调
3. 实现支付重试机制
4. 统一的响应格式处理
5. 完善的日志记录

## 注意事项
1. 所有接口都需要进行适当的权限验证
2. 支付相关接口需要特别注意数据安全性
3. 文件上传接口需要注意文件大小限制

## 数据库设计

### 1. 用户表 (users)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| open_id | VARCHAR(64) | 微信 open id |
| phone | CHAR(20) | 手机号(加密) |
| password | CHAR(60) | 密码(加密) |
| nick_name | VARCHAR(64) | 昵称 |
| avatar_url | VARCHAR(500) | 头像URL |
| role | TINYINT | 角色: 1:用户 2:车位所有者 3:管理员 |
| status | TINYINT | 状态: 0:禁用 1:正常 |
| source_from | TINYINT | 来源: 1:微信 2:APP |

### 2. 停车位表 (parking_spots)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| owner_id | BIGINT | 所有者ID |
| location | VARCHAR(255) | 位置描述 |
| longitude | DECIMAL(10,6) | 经度 |
| latitude | DECIMAL(10,6) | 纬度 |
| description | TEXT | 详细描述 |
| price | DECIMAL(10,2) | 每小时价格 |
| images | VARCHAR(500) | 图片列表 |
| rules | VARCHAR(1000) | 可用时段规则 |
| facilities | VARCHAR(500) | 设施列表 |
| status | TINYINT | 状态: 0:待审核 1:已审核 2:可用 3:已拒绝 |

### 3. 订单表 (orders)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| parking_spot_id | BIGINT | 停车位ID |
| owner_id | BIGINT | 所有者ID |
| parking_occupied_id | BIGINT | 占用记录ID |
| car_number | VARCHAR(30) | 车牌号 |
| amount | DECIMAL(10,2) | 订单金额 |
| refund_amount | DECIMAL(10,2) | 退款金额 |
| transaction_id | VARCHAR(64) | 支付交易ID |
| status | TINYINT | 状态: 0:待支付 1:已预订 2:已确认 3:进行中 4:已完成 5:取消中 6:已取消 7:退款中 8:已退款 9:已逾期 10:逾期待支付 11:临时离开 |

### 4. 停车占用表 (parking_occupied)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| parking_spot_id | BIGINT | 停车位ID |
| parking_day | DATE | 停车日期 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |

### 5. 收藏表 (favorites)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| parking_spot_id | BIGINT | 停车位ID |

### 6. 评价表 (reviews)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单ID |
| user_id | BIGINT | 用户ID |
| parking_spot_id | BIGINT | 停车位ID |
| rating | TINYINT | 评分(1-5) |
| content | TEXT | 评价内容 |
| images | JSON | 图片URL列表 |

### 7. 支付回调日志表 (pay_notify_log)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| order_id | VARCHAR(255) | 订单ID |
| trade_no | VARCHAR(255) | 交易号 |
| pay_type | VARCHAR(50) | 支付类型: wechat/alipay |
| notify_time | DATETIME | 回调时间 |
| notify_params | TEXT | 回调参数 |
| status | VARCHAR(20) | 处理状态: SUCCESS/FAILED |
| error_msg | TEXT | 错误信息 |

## 数据库特点
1. 使用软删除机制 (deleted_at)
2. 包含创建和更新时间戳
3. 适当的索引设计
4. 关键字段加密存储
5. 完整的外键关系设计
