-- ============================================================
-- Goodit Assignment - DB 스키마
-- ============================================================

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS member;
-- ------------------------------------------------------------
-- 1. member 테이블
--    kr.goodit.assignment.member.domain.Member 엔티티 매핑
-- ------------------------------------------------------------
CREATE TABLE member (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)   NOT NULL,
    password    VARCHAR(60)   NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    CONSTRAINT uk_member_username UNIQUE (username),
    CONSTRAINT uk_member_email    UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. orders 테이블
--    kr.goodit.assignment.order.domain.Order 엔티티 매핑
--    프로그램 실행 시, DataInit.java가 자동 삽입
-- ------------------------------------------------------------
CREATE TABLE orders (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    price         BIGINT       NOT NULL,
    order_date    DATE         NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
