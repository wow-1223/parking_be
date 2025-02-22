
-- users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(64) UNIQUE NOT NULL COMMENT 'wechat open id',
    phone CHAR(20) UNIQUE COMMENT 'phone (encrypted)',
    password CHAR(60) COMMENT 'password (encrypted)',
    nick_name VARCHAR(64) COMMENT 'nick name',
    avatar_url VARCHAR(500) COMMENT 'avatar url',
    role TINYINT NOT NULL DEFAULT 1 COMMENT 'role: 1: user | 2: owner | 3: admin',
    status TINYINT NOT NULL DEFAULT 1 COMMENT 'status: 0: disabled | 1: active',
    source_from TINYINT NOT NULL DEFAULT 1 COMMENT 'source from: 1: wechat | 2: app',
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
#     coordinate POINT SRID 4326 NOT NULL COMMENT 'coordinate',
    description TEXT COMMENT 'description',
    price DECIMAL(10,2) NOT NULL COMMENT 'price for per hour',
    images VARCHAR(500) COMMENT 'image list',
--     mode TINYINT NOT NULL DEFAULT 1 COMMENT 'mode: 1: daily | 2: weekly | 3: monthly',
    rules VARCHAR(1000) COMMENT 'rule list for available periods',
    facilities VARCHAR(500) COMMENT 'facility list',
    status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0: pending | 1: approved | 2: available | 3: rejected',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_owner_id (owner_id)
#     SPATIAL INDEX idx_coordinate (coordinate)
) COMMENT 'parking spots table';

-- -- parking periods table
-- CREATE TABLE parking_periods (
--    id BIGINT AUTO_INCREMENT PRIMARY KEY,
--    parking_spot_id BIGINT NOT NULL COMMENT 'parking spots id',
--    mode TINYINT NOT NULL DEFAULT 1 COMMENT 'mode: 0: hourly | 1: daily | 2: weekly | 3: monthly',
--     -- 对于 weekly 规则，存储星期几，用逗号分隔，如 1,3,5 表示周一、周三、周五
--     -- 对于 monthly 规则，存储日期范围，如 10-20 表示每月 10 号到 20 号
--    specific_days_or_range VARCHAR(50) NULL COMMENT 'specific days or range',
--    start_time TIME NOT NULL COMMENT 'start time',
--    end_time TIME NOT NULL COMMENT 'end time',
-- --    status TINYINT NOT NULL DEFAULT 2 COMMENT 'status: 1: occupied | 2: available',
--    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
--    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
--    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
--    INDEX idx_parking_spot_id (parking_spot_id)
-- --     FOREIGN KEY (owner_id) REFERENCES users(id)
-- ) COMMENT 'parking periods table';

-- parking occupied table
CREATE TABLE parking_occupied (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spot id',
--     car_number VARCHAR(20) NOT NULL COMMENT 'car number',
    parking_day DATE NOT NULL COMMENT 'day',
    start_time DATETIME NOT NULL COMMENT 'start time',
    end_time DATETIME NOT NULL COMMENT 'end time',
--     parking_interval POINT SRID 0 NOT NULL COMMENT 'parking interval',
--     status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0: reserved | 1: in use | 2: leave temporarily | 3: completed | 4:overdue',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_parking_spot_id (parking_spot_id)
--     SPATIAL INDEX idx_parking_point (parking_interval)
--     FOREIGN KEY (owner_id) REFERENCES users(id)
) COMMENT 'parking occupied table';


-- orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'user id',
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spots id',
    owner_id BIGINT NOT NULL COMMENT 'owner id',
--     parking_period_id BIGINT NOT NULL COMMENT 'parking period id',
    parking_occupied_id BIGINT NOT NULL COMMENT 'parking occupied id',
    car_number VARCHAR(30) NOT NULL COMMENT 'car number',
    amount DECIMAL(10,2) NOT NULL COMMENT 'order amount',
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'refund amount',
    transaction_id VARCHAR(64) UNIQUE COMMENT 'payment transaction id',
    status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0:pending payment | 1:reserved | 2:confirmed | 3:processing | 4:completed | 5: canceling | 6: canceled | 7:refunding | 8:refunded | 9:overdue | 10: overdue pending payment | 11:leave temporarily',
--     payment_id VARCHAR(64) COMMENT 'payment id',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_user_id (user_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_parking_spot_id (parking_spot_id),
    INDEX idx_parking_occupied_id (parking_occupied_id)
--     FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
--     FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT 'orders table';

-- pay notify log table
CREATE TABLE pay_notify_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(255) NOT NULL COMMENT 'order id',
    trade_no VARCHAR(255) NOT NULL COMMENT 'trade no',
    pay_type VARCHAR(50) NOT NULL COMMENT 'pay type: wechat | alipay',
    notify_time DATETIME NOT NULL COMMENT 'notify time',
    notify_params TEXT NOT NULL COMMENT 'notify params',
    status VARCHAR(20) NOT NULL COMMENT 'handle status：SUCCESS/FAILED',
    error_msg TEXT COMMENT 'error message',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    INDEX idx_order_trade_no (order_id, trade_no)
) COMMENT 'pay notify log table';

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
     INDEX idx_parking_spot_id (parking_spot_id)
--     FOREIGN KEY (order_id) REFERENCES orders(id),
--     FOREIGN KEY (user_id) REFERENCES users(id),
--     FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id),
--     UNIQUE KEY uk_order_review (order_id)
) COMMENT 'reviews table';

-- 支付记录表
-- CREATE TABLE payments (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     order_id BIGINT NOT NULL COMMENT 'order id',
--     transaction_id VARCHAR(64) UNIQUE COMMENT 'payment transaction id',
--     amount DECIMAL(10,2) NOT NULL COMMENT 'amount',
--     status VARCHAR(20) NOT NULL COMMENT 'status: 0:unpaid | 1:paying | 2:paid | 3:pay failed | 4:closed | 5:refunding | 6:refunded | 7:refund failed',
--     create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
--     update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
--     deleted_at BIGINT default 0 not null COMMENT 'deleted at',
--     INDEX idx_order_id (order_id)
-- --     FOREIGN KEY (order_id) REFERENCES orders(id)
-- ) COMMENT '支付记录表';

-- 创建空间索引
-- ALTER TABLE parking_spots ADD SPATIAL INDEX idx_location (point(longitude, latitude));