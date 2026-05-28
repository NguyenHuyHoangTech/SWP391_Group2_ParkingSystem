-- Create and use the new Database for the Parking Management System
CREATE DATABASE ParkingManagementSystem;
GO
USE ParkingManagementSystem;
GO

-- 1. ParkingBuilding
CREATE TABLE ParkingBuilding (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    address NVARCHAR(500),
    status VARCHAR(50) CHECK (status IN ('OPEN', 'CLOSED')) DEFAULT 'OPEN'
);

-- 2. Floor
CREATE TABLE Floor (
    id INT IDENTITY(1,1) PRIMARY KEY,
    building_id INT FOREIGN KEY REFERENCES ParkingBuilding(id),
    name NVARCHAR(100) NOT NULL,
    floor_level INT NOT NULL,
    capacity INT NOT NULL DEFAULT 0
);

-- 3. VehicleType
CREATE TABLE VehicleType (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255)
);

-- 4. ParkingZone
CREATE TABLE ParkingZone (
    id INT IDENTITY(1,1) PRIMARY KEY,
    floor_id INT FOREIGN KEY REFERENCES Floor(id),
    name NVARCHAR(100) NOT NULL,
    vehicle_type_id INT FOREIGN KEY REFERENCES VehicleType(id),
    capacity INT NOT NULL DEFAULT 0
);

-- 5. Slot
CREATE TABLE Slot (
    id INT IDENTITY(1,1) PRIMARY KEY,
    zone_id INT FOREIGN KEY REFERENCES ParkingZone(id),
    name VARCHAR(50) NOT NULL,
    status VARCHAR(50) CHECK (status IN ('EMPTY', 'OCCUPIED', 'MAINTENANCE')) DEFAULT 'EMPTY'
);

-- 6. Account
CREATE TABLE Account (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50) CHECK (role IN ('ADMIN', 'MANAGER', 'STAFF', 'USER')) NOT NULL,
    building_id INT NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    status VARCHAR(50) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED')) DEFAULT 'ACTIVE'
);

-- 7. ParkingCard
CREATE TABLE ParkingCard (
    id INT IDENTITY(1,1) PRIMARY KEY,
    card_code VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(50) CHECK (status IN ('AVAILABLE', 'IN_USE', 'LOST', 'BLOCKED')) DEFAULT 'AVAILABLE'
);

-- 8. PricingPolicy
CREATE TABLE PricingPolicy (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    vehicle_type_id INT FOREIGN KEY REFERENCES VehicleType(id),
    status VARCHAR(50) CHECK (status IN ('ACTIVE', 'INACTIVE')) DEFAULT 'ACTIVE'
);

-- 9. PricingBlock
CREATE TABLE PricingBlock (
    id INT IDENTITY(1,1) PRIMARY KEY,
    policy_id INT FOREIGN KEY REFERENCES PricingPolicy(id),
    block_order INT NOT NULL,
    duration_hours INT NOT NULL,
    price FLOAT NOT NULL
);

-- 10. MonthlyTicket
CREATE TABLE MonthlyTicket (
    id INT IDENTITY(1,1) PRIMARY KEY,
    account_id INT FOREIGN KEY REFERENCES Account(id),
    vehicle_type_id INT FOREIGN KEY REFERENCES VehicleType(id),
    license_plate VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED')) DEFAULT 'ACTIVE'
);

-- 11. ParkingSession
CREATE TABLE ParkingSession (
    id INT IDENTITY(1,1) PRIMARY KEY,
    account_id INT NULL FOREIGN KEY REFERENCES Account(id),
    slot_id INT NULL FOREIGN KEY REFERENCES Slot(id),
    vehicle_type_id INT FOREIGN KEY REFERENCES VehicleType(id),
    card_id INT NULL FOREIGN KEY REFERENCES ParkingCard(id),
    license_plate VARCHAR(50) NOT NULL,
    entry_gate NVARCHAR(100) NOT NULL,
    exit_gate NVARCHAR(100) NULL,
    check_in_time DATETIME NULL,
    check_out_time DATETIME NULL,
    status VARCHAR(50) CHECK (status IN ('RESERVED', 'ACTIVE', 'COMPLETED', 'CANCELLED')) DEFAULT 'ACTIVE'
);

-- 12. Payment
CREATE TABLE Payment (
    id INT IDENTITY(1,1) PRIMARY KEY,
    session_id INT NULL FOREIGN KEY REFERENCES ParkingSession(id),
    monthly_ticket_id INT NULL FOREIGN KEY REFERENCES MonthlyTicket(id),
    amount FLOAT NOT NULL,
    payment_method VARCHAR(50) CHECK (payment_method IN ('CASH', 'VNPAY', 'MOMO')),
    status VARCHAR(50) CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT GETDATE()
);

-- 13. SystemConfig
CREATE TABLE SystemConfig (
    id INT IDENTITY(1,1) PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value NVARCHAR(500) NOT NULL,
    description NVARCHAR(MAX),
    building_id INT NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    updated_by INT NULL FOREIGN KEY REFERENCES Account(id),
    updated_at DATETIME DEFAULT GETDATE()
);

-- 14. SystemLog
CREATE TABLE SystemLog (
    id INT IDENTITY(1,1) PRIMARY KEY,
    log_type VARCHAR(50) CHECK (log_type IN ('ERROR', 'AUDIT', 'SLOT_HISTORY')) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    created_by INT NULL FOREIGN KEY REFERENCES Account(id),
    created_at DATETIME DEFAULT GETDATE()
);

-- 15. FeedbackTicket
CREATE TABLE FeedbackTicket (
    id INT IDENTITY(1,1) PRIMARY KEY,
    account_id INT NOT NULL FOREIGN KEY REFERENCES Account(id),
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NOT NULL,
    status VARCHAR(50) CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')) DEFAULT 'OPEN',
    resolved_by INT NULL FOREIGN KEY REFERENCES Account(id),
    created_at DATETIME DEFAULT GETDATE(),
    resolved_at DATETIME NULL
);