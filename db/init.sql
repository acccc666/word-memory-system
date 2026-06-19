-- word-memory-system 数据库初始化脚本
-- 用法：mysql -u root -p < db/init.sql

CREATE DATABASE IF NOT EXISTS word_memory
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE word_memory;

CREATE TABLE IF NOT EXISTS user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(30)  NOT NULL UNIQUE,
    password    VARCHAR(64)  NOT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS word_book (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_name   VARCHAR(50)  NOT NULL,
    intro       VARCHAR(500),
    target_user VARCHAR(100),
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS word (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id  BIGINT       NOT NULL,
    english  VARCHAR(50)  NOT NULL,
    chinese  VARCHAR(200) NOT NULL,
    INDEX idx_book_id (book_id)
);

CREATE TABLE IF NOT EXISTS user_word (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT  NOT NULL,
    word_id      BIGINT  NOT NULL,
    forget_count INT     DEFAULT 0,
    word_status  TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS exam_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT   NOT NULL,
    book_id     BIGINT   NOT NULL,
    exam_num    INT      NOT NULL,
    score       INT      DEFAULT 0,
    set_time    INT      NOT NULL,
    exam_status TINYINT  DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time    DATETIME,
    INDEX idx_user_id (user_id)
);