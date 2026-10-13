
-- Car Reservation System - Database Setup


CREATE DATABASE IF NOT EXISTS car_reservation_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE car_reservation_db;


-- Πίνακας χρηστών (login για όλους τους ρόλους)

CREATE TABLE IF NOT EXISTS users (
    user_id       INT          AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('CUSTOMER','SECRETARY','ADMIN') NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Πίνακας πελατών (συνδέεται με users)

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT          AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(20),
    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);


-- Πίνακας οχημάτων

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id  INT         AUTO_INCREMENT PRIMARY KEY,
    plate       VARCHAR(20) NOT NULL UNIQUE,
    brand       VARCHAR(50) NOT NULL,
    model       VARCHAR(50) NOT NULL,
    category    ENUM('ECONOMY','SUV','LUXURY') NOT NULL,
    status      ENUM('AVAILABLE','RESERVED','MAINTENANCE','DAMAGED')
                    NOT NULL DEFAULT 'AVAILABLE',
    mileage     INT         NOT NULL DEFAULT 0,
    daily_rate  DECIMAL(8,2) NOT NULL DEFAULT 0.00
);


-- Πίνακας κρατήσεων

CREATE TABLE IF NOT EXISTS bookings (
    booking_id  INT          AUTO_INCREMENT PRIMARY KEY,
    customer_id INT          NOT NULL,
    vehicle_id  INT          NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    status      ENUM('ACTIVE','COMPLETED','CANCELLED')
                    NOT NULL DEFAULT 'ACTIVE',
    total_cost  DECIMAL(10,2),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_booking_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS fees (
    fee_id      INT           AUTO_INCREMENT PRIMARY KEY,
    booking_id  INT           NOT NULL,
    reason      VARCHAR(200)  NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fee_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
        ON DELETE RESTRICT
);


INSERT INTO users (username, password_hash, role) VALUES
('admin1',     'admin123',    'ADMIN'),
('secretary1', 'secretary123','SECRETARY'),
('customer1',  'customer123', 'CUSTOMER');

INSERT INTO customers (user_id, full_name, email, phone) VALUES
(3, 'Γιώργης Παπαδόπουλος', 'giorgos@email.com', '6900000001');

INSERT INTO vehicles (plate, brand, model, category, daily_rate) VALUES
('ΑΒΓ-1234', 'Toyota',  'Yaris',   'ECONOMY', 35.00),
('ΔΕΖ-5678', 'Hyundai', 'Tucson',  'SUV',     65.00),
('ΗΘΙ-9012', 'BMW',     '3 Series','LUXURY',  120.00);


