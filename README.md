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
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 登录 | POST | /api/user/login | 微信登录认证 |

#### 2. 停车位管理
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 获取附近停车位 | GET | /api/parking/nearby | 根据地理位置获取附近车位 |
| 搜索停车位 | GET | /api/parking/search | 关键词搜索车位 |
| 获取停车位详情 | GET | /api/parking/{id} | 获取车位详细信息 |

#### 3. 订单管理
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 创建订单 | POST | /api/orders | 创建停车订单 |
| 获取订单列表 | GET | /api/orders | 获取用户订单列表 |
| 取消订单 | POST | /api/orders/{id}/cancel | 取消指定订单 |

#### 4. 收藏管理
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 收藏操作 | POST | /api/favorites | 收藏/取消收藏车位 |
| 获取收藏列表 | GET | /api/favorites | 获取收藏车位列表 |

### 出租方接口

#### 1. 车位管理
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 发布车位 | POST | /api/owner/parking | 发布新车位 |
| 修改车位 | PUT | /api/owner/parking/{id} | 修改车位信息 |
| 获取车位列表 | GET | /api/owner/parking | 获取已发布车位列表 |

#### 2. 订单管理
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 获取订单列表 | GET | /api/owner/orders | 获取车位订单列表 |

#### 3. 统计分析
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 收益统计 | GET | /api/owner/statistics/earnings | 获取收益统计数据 |
| 使用率统计 | GET | /api/owner/statistics/usage | 获取车位使用率统计 |

### 通用接口

#### 1. 文件上传
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 上传图片 | POST | /api/upload/image | 上传图片文件 |

#### 2. 支付相关
| 接口 | 方法 | 路径 | 描述 |
|-----|------|------|------|
| 创建支付订单 | POST | /api/payment/create | 创建支付订单 |
| 查询支付状态 | GET | /api/payment/{orderId}/status | 查询订单支付状态 |

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
