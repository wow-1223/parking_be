
-- users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(64) UNIQUE NOT NULL DEFAULT '' COMMENT 'wechat open id',
    phone CHAR(40) UNIQUE NOT NULL DEFAULT '' COMMENT 'phone (encrypted)',
    password CHAR(60) NOT NULL DEFAULT '' COMMENT 'password (encrypted)',
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
    device_id VARCHAR(50) NOT NULL Default '' COMMENT 'device id',
    location VARCHAR(255) NOT NULL COMMENT 'location',
    longitude DECIMAL(10, 6) NOT NULL COMMENT 'longitude',
    latitude DECIMAL(10, 6) NOT NULL COMMENT 'latitude',
    description TEXT COMMENT 'description',
    price DECIMAL(10,2) NOT NULL COMMENT 'price for per hour',
    images VARCHAR(500) COMMENT 'image list',
--  com.parking.model.vo.parking.ParkingSpotRuleVO
    rules VARCHAR(1000) COMMENT 'rule list for available periods',
    facilities VARCHAR(500) COMMENT 'facility list',
    status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0: approving | 1: approved | 2: available | 3: rejected | 4:breakdown',
    rating DECIMAL(3,2) NOT NULL DEFAULT 0 COMMENT 'rating',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_owner_id (owner_id)
#     SPATIAL INDEX idx_coordinate (coordinate)
) COMMENT 'parking spots table';

-- parking occupied table
CREATE TABLE parking_occupied (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spot id',
    parking_day DATE NOT NULL COMMENT 'day',
    start_time DATETIME NOT NULL COMMENT 'start time',
    end_time DATETIME NOT NULL COMMENT 'end time',
    actual_start_time DATETIME COMMENT 'actual start time',
    actual_end_time DATETIME COMMENT 'actual end time',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_parking_spot_id (parking_spot_id),
    INDEX idx_parking_day (parking_day),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_actual_start_time (actual_start_time),
    INDEX idx_actual_end_time (actual_end_time)
--     SPATIAL INDEX idx_parking_point (parking_interval)
--     FOREIGN KEY (owner_id) REFERENCES users(id)
) COMMENT 'parking occupied table';


-- orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'user id',
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spots id',
    owner_id BIGINT NOT NULL COMMENT 'owner id',
    parking_occupied_id BIGINT NOT NULL COMMENT 'parking occupied id',
    car_number VARCHAR(30) NOT NULL COMMENT 'car number',
    amount DECIMAL(10,2) NOT NULL COMMENT 'order amount',
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'refund amount',
    timeout_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'timeout amount',
    pay_type VARCHAR(20) NOT NULL DEFAULT '' COMMENT 'pay type: WECHAT_PAY | ALIPAY',
    transaction_id VARCHAR(64) UNIQUE COMMENT 'payment transaction id',
    timeout_transaction_id VARCHAR(64) UNIQUE COMMENT 'timeout payment transaction id',
    status TINYINT NOT NULL DEFAULT 0 COMMENT 'status: 0:pending payment | 1:reserved | 2:confirmed | 3:processing | 4:completed | 5: canceling | 6: canceled | 7:refunding | 8:refunded | 9:timeout | 10: timeout pending payment | 11:leave temporarily | 12: user occupied | 13: unknown occupied',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_user_id (user_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_parking_spot_id (parking_spot_id),
    INDEX idx_parking_occupied_id (parking_occupied_id)
) COMMENT 'orders table';

-- parking spot revenue table
CREATE TABLE parking_spot_revenue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spot id',
    owner_id BIGINT NOT NULL COMMENT 'owner id',
    parking_day DATE NOT NULL COMMENT 'day',
    revenue DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'revenue',
    platform_revenue DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'platform revenue',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_parking_spot_id (parking_spot_id),
    INDEX idx_owner_id (owner_id)
) COMMENT 'parking spot revenue table';

-- parking spot withdraw table
CREATE TABLE parking_spot_withdraw_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parking_spot_id BIGINT NOT NULL COMMENT 'parking spot id',
    owner_id BIGINT NOT NULL COMMENT 'owner id',
    amount DECIMAL(10,2) NOT NULL COMMENT 'amount',
    status VARCHAR(20) NOT NULL COMMENT 'handle status：SUCCESS/FAILED',
    error_msg TEXT COMMENT 'error message',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    deleted_at BIGINT default 0 not null COMMENT 'deleted at',
    INDEX idx_parking_spot_id (parking_spot_id),
    INDEX idx_owner_id (owner_id)
) COMMENT 'parking spot withdraw log table';

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
) COMMENT 'reviews table';