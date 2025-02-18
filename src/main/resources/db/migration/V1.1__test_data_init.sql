-- Create database if not exists
CREATE DATABASE IF NOT EXISTS wow_test_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Switch to the database
USE wow_test_db;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
# TRUNCATE TABLE reviews;
TRUNCATE TABLE favorites;
TRUNCATE TABLE pay_notify_log;
TRUNCATE TABLE orders;
TRUNCATE TABLE parking_occupied;
TRUNCATE TABLE parking_spots;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- Users test data
INSERT INTO users (open_id, phone, nick_name, avatar_url, role, status, source_from)
SELECT
    CONCAT('wx_', LPAD(num, 8, '0')) as open_id,
    CONCAT('1', LPAD(FLOOR(RAND() * 999999999), 10, '0')) as phone,
    CONCAT('User', num) as nick_name,
    CONCAT('https://example.com/avatar/', num, '.jpg') as avatar_url,
    CASE
        WHEN num <= 80 THEN 1  -- 80% normal users
        WHEN num <= 95 THEN 2  -- 15% owners
        ELSE 3                 -- 5% admins
        END as role,
    1 as status,
    FLOOR(1 + RAND() * 2) as source_from
FROM (
         SELECT @row := @row + 1 as num
         FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
              (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
              (SELECT @row:=0) t3
         LIMIT 100
     ) numbers;

-- Parking spots test data
INSERT INTO parking_spots (owner_id, location, longitude, latitude, coordinate, description, price, images, rules, facilities, status)
SELECT
    (SELECT id FROM users WHERE role = 2 ORDER BY RAND() LIMIT 1) as owner_id,
    CONCAT('Location ', num) as location,
    113.0 + (RAND() * 2) as longitude,  -- Guangzhou area
    22.0 + (RAND() * 2) as latitude,    -- Guangzhou area
    ST_SRID(POINT(113.0 + (RAND() * 2), 22.0 + (RAND() * 2)), 4326) as coordinate,  -- 使用ST_SRID指定SRID
    CONCAT('Parking spot description ', num) as description,
    ROUND(10 + (RAND() * 40), 2) as price,  -- Price between 10-50
    JSON_ARRAY(
            CONCAT('https://example.com/parking/', num, '_1.jpg'),
            CONCAT('https://example.com/parking/', num, '_2.jpg')
    ) as images,
    JSON_OBJECT(
            'daily', JSON_ARRAY(
            JSON_OBJECT(
                    'start', '08:00',
                    'end', '22:00'
            )
                     )
    ) as rules,
    JSON_ARRAY('camera', 'roof', 'charging') as facilities,
    2 as status  -- available
FROM (SELECT @row := @row + 1 as num FROM
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                         (SELECT @row:=0) t3
      LIMIT 100
     ) numbers;

-- Parking occupied test data
INSERT INTO parking_occupied (parking_spots_id, parking_day, start_time, end_time)
SELECT
    (SELECT id FROM parking_spots ORDER BY RAND() LIMIT 1) as parking_spots_id,
    DATE_FORMAT(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), '%Y-%m-%d') as parking_day,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as start_time,
    DATE_ADD(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), INTERVAL FLOOR(1 + RAND() * 5) HOUR) as end_time
FROM (SELECT @row := @row + 1 as num FROM
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                         (SELECT @row:=0) t3
      LIMIT 100
     ) numbers;

-- Orders test data
INSERT INTO orders (user_id, parking_spots_id, owner_id, parking_occupied_id, car_number, amount, refund_amount, transaction_id, status)
SELECT
    (SELECT id FROM users WHERE role = 1 ORDER BY RAND() LIMIT 1) as user_id,
    po.parking_spots_id,
    (SELECT owner_id FROM parking_spots WHERE id = po.parking_spots_id) as owner_id,
    po.id as parking_occupied_id,
    CONCAT('粤A', LPAD(FLOOR(RAND() * 99999), 5, '0')) as car_number,
    ROUND(50 + (RAND() * 150), 2) as amount,
    ROUND(10 + (RAND() * 30), 2) as refund_amount,
    CONCAT('TXN', LPAD(num, 8, '0')) as transaction_id,
    FLOOR(RAND() * 11) as status
FROM parking_occupied po,
     (SELECT @row := @row + 1 as num FROM
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                         (SELECT @row:=0) t3
      LIMIT 100
     ) numbers;

-- Pay notify log test data
INSERT INTO pay_notify_log (order_id, trade_no, pay_type, notify_time, notify_params, status)
SELECT
    o.id as order_id,
    o.transaction_id as trade_no,
    CASE WHEN RAND() < 0.5 THEN 'wechat' ELSE 'alipay' END as pay_type,
    o.create_time as notify_time,
    JSON_OBJECT(
            'amount', o.amount,
            'status', 'SUCCESS',
            'transaction_id', o.transaction_id
    ) as notify_params,
    'SUCCESS' as status
FROM orders o
LIMIT 100;

-- Favorites test data
INSERT INTO favorites (user_id, parking_spot_id)
SELECT
    (SELECT id FROM users WHERE role = 1 ORDER BY RAND() LIMIT 1) as user_id,
    (SELECT id FROM parking_spots ORDER BY RAND() LIMIT 1) as parking_spot_id
FROM (SELECT @row := @row + 1 as num FROM
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                         (SELECT @row:=0) t3
      LIMIT 100
     ) numbers
ON DUPLICATE KEY UPDATE deleted_at = 0;

-- Reviews test data
# INSERT INTO reviews (order_id, user_id, parking_spot_id, rating, content, images)
# SELECT
#     o.id as order_id,
#     o.user_id,
#     o.parking_spots_id as parking_spot_id,
#     FLOOR(3 + RAND() * 3) as rating,  -- Rating between 3-5
#     CONCAT('Review content for order ', o.id) as content,
#     JSON_ARRAY(
#             CONCAT('https://example.com/review/', o.id, '_1.jpg'),
#             CONCAT('https://example.com/review/', o.id, '_2.jpg')
#     ) as images
# FROM orders o
# WHERE o.status = 4  -- Only completed orders
# LIMIT 100;