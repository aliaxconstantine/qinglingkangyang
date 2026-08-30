-- Qinling wellness crawler platform database schema.
-- MySQL 8.0+ / utf8mb4

CREATE DATABASE IF NOT EXISTS qlky
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE qlky;

CREATE TABLE IF NOT EXISTS crawler (
    crawler_id INT NOT NULL AUTO_INCREMENT,
    crawler_name VARCHAR(255),
    crawler_description VARCHAR(2000),
    crawler_program_path VARCHAR(1000),
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (crawler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_task (
    task_id INT NOT NULL AUTO_INCREMENT,
    crawler_id INT NULL,
    task_name VARCHAR(255),
    task_status VARCHAR(64),
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cron VARCHAR(255),
    interval_seconds INT NULL,
    PRIMARY KEY (task_id),
    KEY idx_crawler_task_crawler_id (crawler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_data_field (
    field_id INT NOT NULL AUTO_INCREMENT,
    crawler_id INT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_type VARCHAR(64),
    field_description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    sortid INT NULL,
    PRIMARY KEY (field_id),
    KEY idx_crawler_data_field_crawler_id (crawler_id),
    UNIQUE KEY uk_crawler_data_field_name (crawler_id, field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_data (
    data_id BIGINT NOT NULL AUTO_INCREMENT,
    crawler_id INT NOT NULL,
    task_id INT NULL,
    data LONGTEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fieldid INT NOT NULL,
    group_id VARCHAR(128) NOT NULL,
    PRIMARY KEY (data_id),
    KEY idx_crawler_data_group (crawler_id, group_id),
    KEY idx_crawler_data_fieldid (fieldid),
    KEY idx_crawler_data_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_system (
    system_id INT NOT NULL AUTO_INCREMENT,
    crawler_id INT NULL,
    proxy VARCHAR(1000),
    user_agent VARCHAR(2000),
    max_retry INT NULL,
    timeout INT NULL,
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (system_id),
    KEY idx_crawler_system_crawler_id (crawler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_system_message (
    messageid BIGINT NOT NULL AUTO_INCREMENT,
    message LONGTEXT,
    createtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (messageid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS crawler_website (
    website_id INT NOT NULL AUTO_INCREMENT,
    crawler_id INT NULL,
    website_url VARCHAR(2000),
    website_name VARCHAR(255),
    website_description VARCHAR(2000),
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (website_id),
    KEY idx_crawler_website_crawler_id (crawler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
