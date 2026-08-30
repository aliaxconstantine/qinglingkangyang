-- Initial crawler definitions, task IDs, and data fields used by bundled scripts.
-- Run this after qlky-schema-mysql.sql. The script is safe to run repeatedly.

USE qlky;

INSERT INTO crawler (crawler_id, crawler_name, crawler_description, crawler_program_path)
VALUES
    (2, '景区数据采集', '导入本地景区 Excel 数据，供旅游大屏使用。', 'D:/code/qlky-project/file/jd/JD_script.py'),
    (3, '特产数据采集', '导入本地特产 Excel 数据，供特产大屏使用。', 'D:/code/qlky-project/file/tc/tc_scipt.py'),
    (4, '康养服务数据采集', '导入本地康养服务 XML 数据。', 'D:/code/qlky-project/file/ky/ky_script.py')
ON DUPLICATE KEY UPDATE
    crawler_name = VALUES(crawler_name),
    crawler_description = VALUES(crawler_description),
    crawler_program_path = VALUES(crawler_program_path);

INSERT INTO crawler_task (task_id, crawler_id, task_name, task_status, cron, interval_seconds)
VALUES
    (1, 2, '景区数据初始化任务', '0', NULL, NULL),
    (2, 3, '特产数据初始化任务', '0', NULL, NULL),
    (3, 4, '康养服务数据初始化任务', '0', NULL, NULL)
ON DUPLICATE KEY UPDATE
    crawler_id = VALUES(crawler_id),
    task_name = VALUES(task_name);

INSERT INTO crawler_data_field (crawler_id, field_name, field_type, field_description, sortid)
VALUES
    (2, 'spot_name', '3', '景点名称', 1),
    (2, 'spot_rating', '4', '景点评分', 2),
    (2, 'strategy_count', '4', '攻略数量', 3),
    (2, 'review_count', '4', '点评数量', 4),
    (2, 'visitor_rate', '1', '游客到访率', 5),
    (2, 'ranking', '3', '景点排名', 6),
    (2, 'secondary_address', '3', '二级地址', 7),
    (2, 'latitude', '1', '纬度', 8),
    (2, 'longitude', '1', '经度', 9),
    (2, 'open_time', '3', '开放时间', 10),
    (2, 'location', '3', '位置', 11),
    (2, 'ticket_price', '3', '门票价格', 12),
    (2, 'best_season', '3', '最佳季节', 13),
    (2, 'image_url', '7', '图片地址', 14),
    (3, 'product_name', '3', '特产名称', 1),
    (3, 'image_url', '7', '图片地址', 2),
    (3, 'origin_place', '3', '产地', 3),
    (3, 'product_type', '3', '特产类型', 4),
    (3, 'description', '2', '特产描述', 5),
    (3, 'detail_address', '3', '详情地址', 6),
    (4, 'shop_id', '3', '店铺 ID', 1),
    (4, 'shop_name', '3', '店铺名称', 2),
    (4, 'review_count', '4', '评论总数', 3),
    (4, 'avg_price', '4', '人均价格', 4),
    (4, 'category', '3', '项目分类', 5),
    (4, 'location', '3', '所在地', 6),
    (4, 'address', '3', '店铺地址', 7),
    (4, 'detail_url', '3', '详情链接', 8),
    (4, 'image_url', '7', '图片链接', 9),
    (4, 'overall_score', '1', '综合评分', 10),
    (4, 'phone', '3', '联系电话', 11),
    (4, 'latitude', '1', '纬度', 12),
    (4, 'longitude', '1', '经度', 13),
    (4, 'province', '3', '省份', 14),
    (4, 'taste_score', '1', '口味评分', 15),
    (4, 'environment_score', '1', '环境评分', 16),
    (4, 'service_score', '1', '服务评分', 17)
ON DUPLICATE KEY UPDATE
    field_type = VALUES(field_type),
    field_description = VALUES(field_description),
    sortid = VALUES(sortid);
