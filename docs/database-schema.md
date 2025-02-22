# 数据库表结构说明

## 1. users (用户表)
| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键ID |
| open_id | VARCHAR(64) | 是 | 微信openid，唯一 |
| phone | CHAR(20) | 否 | 手机号(AES加密) |
| password | CHAR(60) | 否 | 密码(BCrypt加密) |
| nick_name | VARCHAR(64) | 否 | 用户昵称 |
| avatar_url | VARCHAR(500) | 否 | 头像URL |
| role | TINYINT | 是 | 角色: 1=普通用户, 2=车位主, 3=管理员 |
| status | TINYINT | 是 | 状态: 0=禁用, 1=正常 |
| source_from | TINYINT | 是 | 来源: 1=微信, 2=APP |
| create_time | TIMESTAMP | 是 | 创建时间 |
| update_time | TIMESTAMP | 是 | 更新时间 |
| deleted_at | BIGINT | 是 | 删除时间戳(0=未删除) |

## 2. parking_spots (停车位表)
| 字段名 | 类型 | 必填 | 描述                                         |
|--------|------|------|--------------------------------------------|
| id | BIGINT | 是 | 主键ID                                       |
| owner_id | BIGINT | 是 | 车位所有者ID                                    |
| location | VARCHAR(255) | 是 | 位置描述                                       |
| longitude | DECIMAL(10,6) | 是 | 经度                                         |
| latitude | DECIMAL(10,6) | 是 | 纬度                                         |
| description | TEXT | 否 | 详细描述                                       |
| price | DECIMAL(10,2) | 是 | 每小时价格                                      |
| images | VARCHAR(500) | 否 | 图片URL列表(JSON)                              |
| rules | VARCHAR(1000) | 否 | 可用时间规则(JSON) (每天 ｜ 每周几-周几 ｜每月几号-几号 的几点至几点) |
| facilities | VARCHAR(500) | 否 | 配套设施(JSON)                                 |
| status | TINYINT | 是 | 状态: 1=待审核, 2=可用, 3=已拒绝                     |
| create_time | TIMESTAMP | 是 | 创建时间                                       |
| update_time | TIMESTAMP | 是 | 更新时间                                       |
| deleted_at | BIGINT | 是 | 删除时间戳(0=未删除)                               |

## 3. parking_occupied (停车位占用表)
| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键ID |
| parking_spot_id | BIGINT | 是 | 停车位ID |
| parking_day | DATE | 是 | 停车日期 |
| start_time | DATETIME | 是 | 开始时间 |
| end_time | DATETIME | 是 | 结束时间 |
| create_time | TIMESTAMP | 是 | 创建时间 |
| update_time | TIMESTAMP | 是 | 更新时间 |
| deleted_at | BIGINT | 是 | 删除时间戳(0=未删除) |

## 4. orders (订单表)
| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键ID |
| user_id | BIGINT | 是 | 用户ID |
| parking_spot_id | BIGINT | 是 | 停车位ID |
| owner_id | BIGINT | 是 | 车位所有者ID |
| parking_occupied_id | BIGINT | 是 | 占用记录ID |
| car_number | VARCHAR(30) | 是 | 车牌号(AES加密) |
| amount | DECIMAL(10,2) | 是 | 订单金额 |
| refund_amount | DECIMAL(10,2) | 是 | 退款金额 |
| transaction_id | VARCHAR(64) | 否 | 支付交易号 |
| status | TINYINT | 是 | 订单状态 |
| create_time | TIMESTAMP | 是 | 创建时间 |
| update_time | TIMESTAMP | 是 | 更新时间 |
| deleted_at | BIGINT | 是 | 删除时间戳(0=未删除) |

订单状态说明:
- 0: 待支付
- 1: 已预订
- 2: 已确认
- 3: 使用中
- 4: 已完成
- 5: 取消中
- 6: 已取消
- 7: 退款中
- 8: 已退款
- 9: 已逾期
- 10: 逾期待支付
- 11: 临时离开

## 5. pay_notify_log (支付回调日志表)
| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键ID |
| order_id | VARCHAR(255) | 是 | 订单ID |
| trade_no | VARCHAR(255) | 是 | 交易号 |
| pay_type | VARCHAR(50) | 是 | 支付类型: wechat/alipay |
| notify_time | DATETIME | 是 | 回调时间 |
| notify_params | TEXT | 是 | 回调参数(JSON) |
| status | VARCHAR(20) | 是 | 处理状态: SUCCESS/FAILED |
| error_msg | TEXT | 否 | 错误信息 |
| create_time | TIMESTAMP | 是 | 创建时间 |

## 6. favorites (收藏表)
| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | BIGINT | 是 | 主键ID |
| user_id | BIGINT | 是 | 用户ID |
| parking_spot_id | BIGINT | 是 | 停车位ID |
| create_time | TIMESTAMP | 是 | 创建时间 |
| deleted_at | BIGINT | 是 | 删除时间戳(0=未删除) |

[//]: # (## 7. reviews &#40;评价表&#41;)

[//]: # (| 字段名 | 类型 | 必填 | 描述 |)

[//]: # (|--------|------|------|------|)

[//]: # (| id | BIGINT | 是 | 主键ID |)

[//]: # (| order_id | BIGINT | 是 | 订单ID |)

[//]: # (| user_id | BIGINT | 是 | 用户ID |)

[//]: # (| parking_spot_id | BIGINT | 是 | 停车位ID |)

[//]: # (| rating | TINYINT | 是 | 评分&#40;1-5&#41; |)

[//]: # (| content | TEXT | 否 | 评价内容 |)

[//]: # (| images | JSON | 否 | 图片URL列表 |)

[//]: # (| create_time | TIMESTAMP | 是 | 创建时间 |)

[//]: # (| update_time | TIMESTAMP | 是 | 更新时间 |)

[//]: # (| deleted_at | BIGINT | 是 | 删除时间戳&#40;0=未删除&#41; |)

## 注意事项

1. 所有表都采用软删除机制，通过deleted_at字段标记删除状态
2. 时间戳字段统一使用TIMESTAMP类型
3. 涉及金额的字段统一使用DECIMAL(10,2)类型
4. 经纬度使用DECIMAL(10,6)类型以保证精度
5. 敏感信息(手机号、车牌号等)使用AES加密存储
6. 密码使用BCrypt加密存储
7. JSON格式数据使用VARCHAR或TEXT类型存储
8. 主要字段都建立了索引以提升查询性能 