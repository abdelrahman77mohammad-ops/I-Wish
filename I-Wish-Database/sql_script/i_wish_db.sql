DROP DATABASE IF EXISTS i_wish_db;

-- Create database
CREATE DATABASE i_wish_db;

-- Use the database
USE i_wish_db;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    email VARCHAR(50),
    password VARCHAR(50)
);

-- Friends table
CREATE TABLE friends (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    friend_id INT,
    status VARCHAR(20)
);

-- Wishlist items table
CREATE TABLE wishlist_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    item_name VARCHAR(50),
    price DOUBLE,
    status VARCHAR(20)
);

-- Contributions table
CREATE TABLE contributions (
    contribution_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT,
    user_id INT,
    amount DOUBLE
);

-- 1. Insert some test users
INSERT INTO users (username, email, password) VALUES 
('Omar', 'omar@gmail.com', '123456'),
('Ahmed', 'ahmed@gmail.com', '123456'),
('Mohamed', 'mohamed@gmail.com', '123456');

-- 2. Insert test friends (Omar and Ahmed are friends)
INSERT INTO friends (user_id, friend_id, status) VALUES 
(1, 2, 'accepted'),
(2, 1, 'accepted');

-- 3. Insert test wishlist items (Omar added a gift)
INSERT INTO wishlist_items (user_id, item_name, price, status) VALUES 
(1, 'Gaming Headset', 1500.0, 'active');

-- 4. Insert test contributions (Ahmed contributed 500 to Omar's gift)
INSERT INTO contributions (item_id, user_id, amount) VALUES 
(1, 2, 500.0);

-- 5. Test Query: View all users
SELECT * FROM wishlist_items;

-- 6. Test Query: View Omar's wishlist
SELECT * FROM wishlist_items WHERE user_id = 1;