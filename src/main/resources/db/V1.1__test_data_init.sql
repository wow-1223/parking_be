-- Create database if not exists
CREATE DATABASE IF NOT EXISTS wow_test_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Switch to the database
USE wow_test_db;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reviews;
TRUNCATE TABLE favorites;
TRUNCATE TABLE pay_notify_log;
TRUNCATE TABLE orders;
TRUNCATE TABLE parking_occupied;
TRUNCATE TABLE parking_spots;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 直接使用批量插入语句替代存储过程
-- 初始化用户数据
INSERT INTO users (open_id, phone, password, nick_name, avatar_url, role, status, source_from)
SELECT
    CONCAT('test_openid_', num) as open_id,
    CONCAT('nF6mWuDQH9Nexe4K8bQZZw==', FLOOR(10000000 + RAND() * 90000000)) as phone,
    -- 使用BCrypt加密的密码，原密码为123456
    CAST('$2a$10$l9.PDCO6FUWt1rxN5jH0c.QPsI3uW61ITpjw.uPO2mYWvGRxbsqY.' AS CHAR(60)) as password,
    CONCAT('用户', num) as nick_name,
    CONCAT('https://example.com/avatar/', num, '.jpg') as avatar_url,
    CASE
        WHEN num <= 800 THEN 1  -- 80% 普通用户
        WHEN num <= 950 THEN 2  -- 15% 车位所有者
        ELSE 3                  -- 5% 管理员
        END as role,
    1 as status,
    FLOOR(1 + RAND() * 2) as source_from
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 1000
     ) numbers;

-- 初始化停车位数据
INSERT INTO parking_spots (owner_id, location, longitude, latitude, coordinate, description, price, images, rules, facilities, status)
SELECT
    FLOOR(801 + RAND() * 150) as owner_id,
    CONCAT('上海市测试地址', num) as location,
    @longitude := 121.0 + RAND() * 1.0 as longitude,  -- 上海经度范围大约在121-122之间
    @latitude := 31.0 + RAND() * 0.5 as latitude,     -- 上海纬度范围大约在31-31.5之间
    ST_GeomFromText(CONCAT('POINT(', @latitude, ' ', @longitude, ')'), 4326) as coordinate,
    CONCAT('测试停车位描述', num) as description,
    FLOOR(500 + RAND() * 2500) as price,
    JSON_ARRAY(
            CONCAT('https://example.com/parking/', num, '_1.jpg'),
            CONCAT('https://example.com/parking/', num, '_2.jpg')
    ) as images,
    JSON_ARRAY(
            JSON_OBJECT(
                    'type', 'daily',
                    'startTime', '08:00',
                    'endTime', '22:00'
            )
    ) as rules,
    JSON_ARRAY('lighting', 'camera', 'shelter') as facilities,
    2 as status
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 1000
     ) numbers;

-- 初始化停车占用数据
INSERT INTO parking_occupied (parking_spots_id, parking_day, start_time, end_time)
SELECT
    FLOOR(1 + RAND() * 1000) as parking_spots_id,
    DATE_ADD('2024-03-21', INTERVAL FLOOR(RAND() * 30) DAY) as parking_day,
    DATE_ADD('2024-03-21 08:00:00', INTERVAL FLOOR(RAND() * 30) DAY) as start_time,
    DATE_ADD('2024-03-21 10:00:00', INTERVAL FLOOR(RAND() * 30) DAY) as end_time
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 2000
     ) numbers;

-- 初始化订单数据
INSERT INTO orders (user_id, parking_spots_id, owner_id, parking_occupied_id, car_number, amount, transaction_id, status)
SELECT
    FLOOR(1 + RAND() * 800) as user_id,
    FLOOR(1 + RAND() * 1000) as parking_spots_id,
    FLOOR(801 + RAND() * 150) as owner_id,
    num as parking_occupied_id,
    CONCAT('沪', CHAR(65 + FLOOR(RAND() * 26)), FLOOR(10000 + RAND() * 90000)) as car_number,
    FLOOR(500 + RAND() * 2500) as amount,
    CONCAT('wx', SUBSTRING(MD5(RAND()), 1, 28)) as transaction_id,
    FLOOR(1 + RAND() * 11) as status
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 2000
     ) numbers;

-- 初始化收藏数据
INSERT IGNORE INTO favorites (user_id, parking_spot_id)
SELECT
    FLOOR(1 + RAND() * 800) as user_id,
    FLOOR(1 + RAND() * 1000) as parking_spot_id
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 2000
     ) numbers;

-- 初始化评价数据
INSERT INTO reviews (order_id, user_id, parking_spot_id, rating, content, images)
SELECT
    num as order_id,
    FLOOR(1 + RAND() * 800) as user_id,
    FLOOR(1 + RAND() * 1000) as parking_spot_id,
    FLOOR(3 + RAND() * 3) as rating,
    CONCAT('测试评价内容', num) as content,
    JSON_ARRAY(
            CONCAT('https://example.com/review/', num, '_1.jpg'),
            CONCAT('https://example.com/review/', num, '_2.jpg')
    ) as images
FROM (
         SELECT @row := @row + 1 as num
         FROM (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t1,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t2,
              (
                  SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                  SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) t3,
              (SELECT @row:=0) t4
         LIMIT 1000
     ) numbers;