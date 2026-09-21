-- ============================================================
--  I-Wish  ·  MySQL schema + sample data
--  Run this once in MySQL Workbench (or: mysql -u root -p < schema.sql)
-- ============================================================

CREATE DATABASE IF NOT EXISTS iwish CHARACTER SET utf8mb4;
USE iwish;

-- users --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,          -- SHA-256 hex (never plain text)
    display_name  VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- friendships (friend requests + accepted friends) -------------
CREATE TABLE IF NOT EXISTS friendships (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    addressee_id INT NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / ACCEPTED
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_pair (requester_id, addressee_id),
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (addressee_id) REFERENCES users(id)
);

-- catalog of items the admin adds (users build wish lists from these)
CREATE TABLE IF NOT EXISTS catalog_items (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL,
    description VARCHAR(255),
    price       DECIMAL(10,2) NOT NULL
);

-- a user's wish list entries ----------------------------------
CREATE TABLE IF NOT EXISTS wish_items (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    catalog_item_id INT NOT NULL,
    purchased       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)         REFERENCES users(id),
    FOREIGN KEY (catalog_item_id) REFERENCES catalog_items(id)
);

-- money contributed by friends toward a wish item -------------
CREATE TABLE IF NOT EXISTS contributions (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    wish_item_id INT NOT NULL,
    buyer_id     INT NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wish_item_id) REFERENCES wish_items(id),
    FOREIGN KEY (buyer_id)     REFERENCES users(id)
);

-- notifications (stored so offline users get them at next login)
CREATE TABLE IF NOT EXISTS notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    message    VARCHAR(255) NOT NULL,
    type       VARCHAR(30)  NOT NULL,       -- ITEM_FUNDED / ITEM_BOUGHT / FRIEND_REQUEST / GENERAL
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- sample catalog items ----------------------------------------
INSERT INTO catalog_items (name, description, price) VALUES
    ('Wireless Headphones', 'Over-ear Bluetooth headphones', 1200.00),
    ('Coffee Mug',          'Ceramic 350 ml',               90.00),
    ('Mechanical Keyboard', 'RGB, blue switches',           1500.00),
    ('The Alchemist (Novel)','Paulo Coelho',                150.00),
    ('Smart Watch',         'Fitness tracker',              2200.00);
