CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(20) NOT NULL,
    nickname VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(255),
    create_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(128),
    description VARCHAR(255),
    address VARCHAR(255),
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT,
    name VARCHAR(128),
    description VARCHAR(255),
    price DECIMAL(10,2),
    category VARCHAR(64),
    status VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    merchant_id BIGINT,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    create_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    dish_id BIGINT,
    quantity INT,
    price DECIMAL(10,2)
);
