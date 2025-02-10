-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(64) UNIQUE NOT NULL COMMENT '微信openid',
    nick_name VARCHAR(64) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/OWNER/ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) COMMENT '用户表';

-- 停车位表
CREATE TABLE parking_spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL COMMENT '车位所有者ID',
    location VARCHAR(255) NOT NULL COMMENT '位置描述',
    latitude DOUBLE NOT NULL COMMENT '纬度',
    longitude DOUBLE NOT NULL COMMENT '经度',
    description TEXT COMMENT '详细描述',
    price DECIMAL(10,2) NOT NULL COMMENT '每小时价格',
    images JSON COMMENT '图片列表',
    rules JSON COMMENT '使用规则',
    facilities JSON COMMENT '设施列表',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/approved/rejected/available/occupied',
    current_order_id BIGINT COMMENT '当前订单ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (owner_id) REFERENCES users(id)
) COMMENT '停车位表';

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parking_spot_id BIGINT NOT NULL COMMENT '停车位ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    car_number VARCHAR(20) NOT NULL COMMENT '车牌号',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/paid/ongoing/completed/cancelled',
    payment_id VARCHAR(64) COMMENT '支付ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT '订单表';

-- 收藏表
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parking_spot_id BIGINT NOT NULL COMMENT '停车位ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
    UNIQUE KEY uk_user_parking (user_id, parking_spot_id)
) COMMENT '收藏表';

-- 评价表
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parking_spot_id BIGINT NOT NULL COMMENT '停车位ID',
    rating TINYINT NOT NULL COMMENT '评分: 1-5',
    content TEXT COMMENT '评价内容',
    images JSON COMMENT '图片列表',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
    UNIQUE KEY uk_order_review (order_id)
) COMMENT '评价表';

-- 支付记录表
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    transaction_id VARCHAR(64) UNIQUE COMMENT '微信支付交易号',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) NOT NULL COMMENT '状态: pending/success/failed',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id)
) COMMENT '支付记录表';

-- 创建空间索引
ALTER TABLE parking_spots ADD SPATIAL INDEX idx_location (point(longitude, latitude));

-- 创建普通索引
CREATE INDEX idx_user_openid ON users(open_id);
CREATE INDEX idx_parking_status ON parking_spots(status);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_time ON orders(start_time, end_time); 