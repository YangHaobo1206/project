CREATE TABLE IF NOT EXISTS t_user (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(20)
    );

CREATE TABLE IF NOT EXISTS t_merchant (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(100),
    logo VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(20),
    business_hours VARCHAR(100),
    approved BOOLEAN,
    owner_user_id BIGINT
    );

CREATE TABLE IF NOT EXISTS t_dish (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      merchant_id BIGINT,
                                      name VARCHAR(100),
    image VARCHAR(255),
    category VARCHAR(50),
    price DECIMAL(10,2),
    on_sale BOOLEAN,
    description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS t_order (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT,
                                       merchant_id BIGINT,
                                       status VARCHAR(50),
    total_amount DECIMAL(10,2),
    create_time TIMESTAMP,
    pay_time TIMESTAMP,
    finish_time TIMESTAMP,
    address VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS t_order_item (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            order_id BIGINT,
                                            dish_id BIGINT,
                                            dish_name VARCHAR(100),
    price DECIMAL(10,2),
    quantity INT
    );
