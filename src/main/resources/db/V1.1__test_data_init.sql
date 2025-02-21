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
    -- 生成唯一的手机号：13800000000 到 13800999999
    CONCAT('nF6mWuDQH9Nexe4K8bQZZw==', LPAD(num + 10000000, 8, '0')) as phone,
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
INSERT INTO parking_spots (
    owner_id, location, longitude, latitude, coordinate, description,
    price, images, rules, facilities, status
)
SELECT
    FLOOR(801 + RAND() * 150) as owner_id,
    CONCAT('上海市测试地址', num) as location,
    @longitude := 121.0 + RAND() * 1.0 as longitude,
    @latitude := 31.0 + RAND() * 0.5 as latitude,
    ST_GeomFromText(CONCAT('POINT(', @latitude, ' ', @longitude, ')'), 4326) as coordinate,
    CONCAT('测试停车位描述', num) as description,
    FLOOR(500 + RAND() * 2500) / 100 as price,
    JSON_ARRAY(
            CONCAT('https://example.com/parking/spot', num, '_1.jpg'),
            CONCAT('https://example.com/parking/spot', num, '_2.jpg')
    ) as images,
    -- 生成停车规则JSON，使用随机时间
    JSON_ARRAY(
        -- 每日规则
            (SELECT JSON_OBJECT(
                            'spotModeStr', 'daily',
                            'endTimeStr', @start_time1 := TIME_FORMAT(FROM_UNIXTIME(FLOOR(6 * 3600 + RAND() * 6 * 3600)), '%H:%i'),
                            'startTimeStr', TIME_FORMAT(FROM_UNIXTIME(
                                                              FLOOR(TIME_TO_SEC(@start_time1) + 7200 + RAND() * (86400 - TIME_TO_SEC(@start_time1) - 7200))
                                                      ), '%H:%i'),
                            'specificDatesStr', JSON_ARRAY(),
                            'specificWeekDaysStr', JSON_ARRAY(),
                            'specificMonthDateRanges', JSON_ARRAY()
                    )),
        -- 每周规则
            (SELECT JSON_OBJECT(
                            'spotModeStr', 'weekly',
                            'endTimeStr', @start_time2 := TIME_FORMAT(FROM_UNIXTIME(FLOOR(8 * 3600 + RAND() * 4 * 3600)), '%H:%i'),
                            'startTimeStr', TIME_FORMAT(FROM_UNIXTIME(
                                                              FLOOR(TIME_TO_SEC(@start_time2) + 7200 + RAND() * (86400 - TIME_TO_SEC(@start_time2) - 7200))
                                                      ), '%H:%i'),
                            'specificDatesStr', JSON_ARRAY(),
                            'specificWeekDaysStr', JSON_ARRAY('1', '2', '3', '4', '5'),
                            'specificMonthDateRanges', JSON_ARRAY()
                    )),
        -- 特定日期规则
            (SELECT JSON_OBJECT(
                            'spotModeStr', 'specific date',
                            'endTimeStr', @start_time3 := TIME_FORMAT(FROM_UNIXTIME(FLOOR(0 * 3600 + RAND() * 12 * 3600)), '%H:%i'),
                            'startTimeStr', TIME_FORMAT(FROM_UNIXTIME(
                                                              FLOOR(TIME_TO_SEC(@start_time3) + 7200 + RAND() * (86400 - TIME_TO_SEC(@start_time3) - 7200))
                                                      ), '%H:%i'),
                            'specificDatesStr', JSON_ARRAY('2024-01-01', '2024-02-10', '2024-05-01'),
                            'specificWeekDaysStr', JSON_ARRAY(),
                            'specificMonthDateRanges', JSON_ARRAY()
                    )),
        -- 每月规则
            (SELECT JSON_OBJECT(
                            'spotModeStr', 'monthly',
                            'endTimeStr', @start_time4 := TIME_FORMAT(FROM_UNIXTIME(FLOOR(9 * 3600 + RAND() * 3 * 3600)), '%H:%i'),
                            'startTimeStr', TIME_FORMAT(FROM_UNIXTIME(
                                                              FLOOR(TIME_TO_SEC(@start_time4) + 7200 + RAND() * (86400 - TIME_TO_SEC(@start_time4) - 7200))
                                                      ), '%H:%i'),
                            'specificDatesStr', JSON_ARRAY(),
                            'specificWeekDaysStr', JSON_ARRAY(),
                            'specificMonthDateRanges', JSON_ARRAY(
                                    JSON_OBJECT('startDay', 1, 'endDay', 5),
                                    JSON_OBJECT('startDay', 15, 'endDay', 20)
                                                       )
                    ))
    ) as rules,
    -- 生成设施JSON
    JSON_ARRAY(
            CASE WHEN RAND() > 0.5 THEN 'CAMERA' ELSE NULL END,
            CASE WHEN RAND() > 0.5 THEN 'LIGHTING' ELSE NULL END,
            CASE WHEN RAND() > 0.7 THEN 'CHARGING' ELSE NULL END,
            CASE WHEN RAND() > 0.8 THEN 'SHELTER' ELSE NULL END
    ) as facilities,
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
INSERT INTO parking_occupied (parking_spot_id, parking_day, start_time, end_time)
SELECT
    FLOOR(1 + RAND() * 1000) as parking_spot_id,
    @day := DATE_ADD('2024-03-21', INTERVAL FLOOR(RAND() * 30) DAY) as parking_day,
    @start := TIMESTAMP(
            @day,
            TIME_FORMAT(FROM_UNIXTIME(FLOOR(8 * 3600 + RAND() * 8 * 3600)), '%H:%i:%s')
              ) as start_time,
    TIMESTAMP(
            @day,
            TIME_FORMAT(FROM_UNIXTIME(
                                TIME_TO_SEC(TIME(@start)) + 7200 + RAND() * (86400 - TIME_TO_SEC(TIME(@start)) - 7200)
                        ), '%H:%i:%s')
    ) as end_time
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
INSERT INTO orders (user_id, parking_spot_id, owner_id, parking_occupied_id, car_number, amount, transaction_id, status)
SELECT
    FLOOR(1 + RAND() * 800) as user_id,
    FLOOR(1 + RAND() * 1000) as parking_spot_id,
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