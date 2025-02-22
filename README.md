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
- 取消停车位

#### 出租方 (com.parking.controller.owner)
- 发布停车位信息
- 管理停车位信息
- 更新停车位状态
- 查看停车位状态
- 获取使用统计

### 3. 订单管理模块
#### 用户端 (com.parking.controller.user)
- 创建订单
- 查询订单列表
- 取消订单
- 订单支付 todo

#### 出租方 (com.parking.controller.owner)
- 订单确认/拒绝 todo
- 查看订单列表
- 订单统计

### 4. 评价模块 (com.parking.controller.user) todo
- 提交评价
- 查看评价
- 评分管理

### 5. 收益管理模块 (com.parking.controller.owner)
- 收益统计
- 收益明细
- 收益报表
