
-- users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(64) UNIQUE NOT NULL COMMENT 'wechat open id',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT 'phone',
    nick_name VARCHAR(64) COMMENT 'nick name',
    avatar_url VARCHAR(255) COMMENT 'avatar url',
    role TINYINT NOT NULL DEFAULT 1 COMMENT 'role: 1: user | 2: owner | 3: admin',
    status TINYINT NOT NULL DEFAULT 1 COMMENT 'status: 1: active | 0: disabled',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at'
) COMMENT 'users table';

-- parking spots table
CREATE TABLE parking_spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL COMMENT 'owner id',
    location VARCHAR(255) NOT NULL COMMENT 'location',
    longitude DECIMAL(10, 6) NOT NULL COMMENT 'longitude',
    latitude DECIMAL(10, 6) NOT NULL COMMENT 'latitude',
--     coordinate POINT NOT NULL COMMENT 'coordinate',
    description TEXT COMMENT 'description',
    price DECIMAL(10,2) NOT NULL COMMENT 'price for per hour',
    images JSON COMMENT 'image list',
    rules JSON COMMENT 'rule list',
    facilities JSON COMMENT 'facility list',
--     status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0: pending | 1: approved | 2: rejected | 3: available | 4:occupied',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_owner_id (owner_id),
--     SPATIAL INDEX idx_coordinate (point(longitude, latitude))
) COMMENT 'parking spots table';

-- parking periods table
CREATE TABLE parking_periods (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   parking_spots_id BIGINT NOT NULL COMMENT 'parking spots id',
   car_number VARCHAR(20) NOT NULL COMMENT 'car number',
   start_time TIMESTAMP NOT NULL COMMENT 'start time',
   end_time TIMESTAMP NOT NULL COMMENT 'end time',
   status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0: pending | 1: approved | 2: rejected | 3: available | 4:occupied',
   create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
   update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
   deleted_at BIGINT default 0 not null COMMENT 'deleted at',
   INDEX idx_parking_spot_id (parking_spots_id)
--     FOREIGN KEY (owner_id) REFERENCES users(id)
) COMMENT 'parking periods table';

-- orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'user id',
    parking_spots_id BIGINT NOT NULL COMMENT 'parking spots id',
    parking_period_id BIGINT NOT NULL COMMENT 'parking period id',
    amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0:pending payment | 1:paid | 2:in use | 3:completed | 4:canceled | 5:expired',
    payment_id VARCHAR(64) COMMENT 'payment id',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_user_id (user_id),
    INDEX idx_parking_spot_id (parking_spots_id),
    INDEX idx_parking_period_id (parking_period_id)
--     FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
--     FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT 'orders table';

-- favorites table
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'user id',
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spots id',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
--     update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_user_id (user_id),
    UNIQUE INDEX idx_user_parking (user_id, parking_spot_id)
) COMMENT 'favorites table';

-- reviews table
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT 'order id',
    user_id BIGINT NOT NULL COMMENT 'user id',
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spot id',
    rating TINYINT NOT NULL COMMENT 'rating: 1-5',
    content TEXT COMMENT 'content',
    images JSON COMMENT 'image url list',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parking_spot_id (parking_spot_id);
--     FOREIGN KEY (order_id) REFERENCES orders(id),
--     FOREIGN KEY (user_id) REFERENCES users(id),
--     FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
--     UNIQUE KEY uk_order_review (order_id)
) COMMENT 'reviews table';

-- 支付记录表
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT 'order id',
    transaction_id VARCHAR(64) UNIQUE COMMENT 'payment transaction id',
    amount DECIMAL(10,2) NOT NULL COMMENT 'amount',
    status VARCHAR(20) NOT NULL COMMENT 'status: 0:unpaid | 1:paying | 2:paid | 3:pay failed | 4:closed | 5:refunding | 6:refunded | 7:refund failed',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_order_id (order_id)
--     FOREIGN KEY (order_id) REFERENCES orders(id)
) COMMENT '支付记录表';

-- 创建空间索引
-- ALTER TABLE parking_spots ADD SPATIAL INDEX idx_location (point(longitude, latitude));