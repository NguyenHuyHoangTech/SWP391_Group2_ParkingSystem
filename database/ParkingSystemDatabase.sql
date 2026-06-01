-- Create and use the new Database for the Parking Management System
CREATE DATABASE ParkingManagementSystem;
GO
USE ParkingManagementSystem;
GO

-- 1. ParkingBuilding
CREATE TABLE ParkingBuilding (
    id int IDENTITY(1,1) PRIMARY KEY,
    name nvarchar(255) NOT NULL,
    address varchar(255) NULL,
    status varchar(255) NULL
);

-- 2. VehicleType
CREATE TABLE VehicleType (
    id int IDENTITY(1,1) PRIMARY KEY,
    name varchar(255) NULL,
    description nvarchar(255) NULL
);

-- 3. Account
CREATE TABLE Account (
    id int IDENTITY(1,1) PRIMARY KEY,
    username varchar(255) NULL UNIQUE,
    password varchar(255) NOT NULL,
    email varchar(255) NULL,
    phone varchar(255) NULL,
    role varchar(255) NULL,
    building_id int NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    status varchar(255) NULL
);

-- 4. Floor
CREATE TABLE Floor (
    id int IDENTITY(1,1) PRIMARY KEY,
    building_id int NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    name nvarchar(100) NOT NULL,
    floor_level int NOT NULL,
    capacity int NOT NULL,
    manager_id int NULL FOREIGN KEY REFERENCES Account(id)
);

-- 5. ParkingZone
CREATE TABLE ParkingZone (
    id int IDENTITY(1,1) PRIMARY KEY,
    floor_id int NULL FOREIGN KEY REFERENCES Floor(id),
    name nvarchar(100) NOT NULL,
    vehicle_type_id int NULL FOREIGN KEY REFERENCES VehicleType(id),
    capacity int NOT NULL
);

-- 6. Slot
CREATE TABLE Slot (
    id int IDENTITY(1,1) PRIMARY KEY,
    zone_id int NULL FOREIGN KEY REFERENCES ParkingZone(id),
    name varchar(255) NULL,
    status varchar(255) NULL
);

-- 7. ParkingCard
CREATE TABLE ParkingCard (
    id int IDENTITY(1,1) PRIMARY KEY,
    card_code varchar(100) NOT NULL UNIQUE,
    status varchar(50) NULL
);

-- 8. PricingPolicy
CREATE TABLE PricingPolicy (
    id int IDENTITY(1,1) PRIMARY KEY,
    name nvarchar(255) NOT NULL,
    vehicle_type_id int NULL FOREIGN KEY REFERENCES VehicleType(id),
    status varchar(50) NULL
);

-- 9. PricingBlock
CREATE TABLE PricingBlock (
    id int IDENTITY(1,1) PRIMARY KEY,
    policy_id int NULL FOREIGN KEY REFERENCES PricingPolicy(id),
    block_order int NOT NULL,
    duration_hours int NOT NULL,
    price float NOT NULL,
    pricing_policy_id int NULL FOREIGN KEY REFERENCES PricingPolicy(id)
);

-- 10. MonthlyTicket
CREATE TABLE MonthlyTicket (
    id int IDENTITY(1,1) PRIMARY KEY,
    account_id int NULL FOREIGN KEY REFERENCES Account(id),
    vehicle_type_id int NULL FOREIGN KEY REFERENCES VehicleType(id),
    license_plate varchar(50) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    status varchar(50) NULL
);

-- 11. ParkingSession
CREATE TABLE ParkingSession (
    id int IDENTITY(1,1) PRIMARY KEY,
    account_id int NULL FOREIGN KEY REFERENCES Account(id),
    slot_id int NULL FOREIGN KEY REFERENCES Slot(id),
    vehicle_type_id int NULL FOREIGN KEY REFERENCES VehicleType(id),
    card_id int NULL FOREIGN KEY REFERENCES ParkingCard(id),
    license_plate varchar(255) NULL,
    entry_gate varchar(255) NULL,
    exit_gate varchar(255) NULL,
    check_in_time datetime NULL,
    check_out_time datetime NULL,
    status varchar(255) NULL,
    building_id int NOT NULL FOREIGN KEY REFERENCES ParkingBuilding(id)
);

-- 12. Payment
CREATE TABLE Payment (
    id int IDENTITY(1,1) PRIMARY KEY,
    session_id int NULL FOREIGN KEY REFERENCES ParkingSession(id),
    monthly_ticket_id int NULL FOREIGN KEY REFERENCES MonthlyTicket(id),
    amount float NOT NULL,
    payment_method varchar(50) NULL,
    status varchar(50) NULL,
    created_at datetime NULL
);

-- 13. SystemConfig
CREATE TABLE SystemConfig (
    id int IDENTITY(1,1) PRIMARY KEY,
    config_key varchar(100) NOT NULL UNIQUE,
    config_value nvarchar(500) NOT NULL,
    description nvarchar(MAX) NULL,
    building_id int NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    updated_by int NULL FOREIGN KEY REFERENCES Account(id),
    updated_at datetime NULL
);

-- 14. SystemLog
CREATE TABLE SystemLog (
    id int IDENTITY(1,1) PRIMARY KEY,
    log_type varchar(50) NOT NULL,
    message nvarchar(MAX) NOT NULL,
    created_by int NULL FOREIGN KEY REFERENCES Account(id),
    created_at datetime NULL
);

-- 15. FeedbackTicket
CREATE TABLE FeedbackTicket (
    id int IDENTITY(1,1) PRIMARY KEY,
    account_id int NOT NULL FOREIGN KEY REFERENCES Account(id),
    title nvarchar(255) NOT NULL,
    description nvarchar(MAX) NOT NULL,
    status varchar(50) NULL,
    resolved_by int NULL FOREIGN KEY REFERENCES Account(id),
    created_at datetime NULL,
    resolved_at datetime NULL
);

-- 16. Booking
CREATE TABLE Booking (
    id int IDENTITY(1,1) PRIMARY KEY,
    account_id int NOT NULL FOREIGN KEY REFERENCES Account(id),
    building_id int NOT NULL FOREIGN KEY REFERENCES ParkingBuilding(id),
    cancelled_at datetime2 NULL,
    created_at datetime2 NULL,
    end_time datetime2 NOT NULL,
    license_plate varchar(255) NOT NULL,
    start_time datetime2 NOT NULL,
    status varchar(255) NOT NULL,
    vehicle_type_id int NOT NULL FOREIGN KEY REFERENCES VehicleType(id)
);
GO

----------------------------------------------------
-- INSERT DATA FROM CURRENT DATABASE
----------------------------------------------------

SET IDENTITY_INSERT ParkingBuilding ON;
INSERT INTO ParkingBuilding (id, name, address, status) VALUES 
(1, 'Main Parking Building', 'FPT University', 'OPEN'),
(2, 'Main Parking Building', 'FPT University', 'OPEN');
SET IDENTITY_INSERT ParkingBuilding OFF;

SET IDENTITY_INSERT VehicleType ON;
INSERT INTO VehicleType (id, name, description) VALUES 
(1, 'Car', 'Standard Car');
SET IDENTITY_INSERT VehicleType OFF;

SET IDENTITY_INSERT Account ON;
INSERT INTO Account (id, username, password, email, phone, role, building_id, status) VALUES 
(20, 'manager1', '123456', 'manager1@example.com', '0900000001', 'MANAGER', 1, 'ACTIVE'),
(22, 'e2e_staff_99', 'password123', 'e2e_staff_99@example.com', '0987654321', 'STAFF', 1, 'ACTIVE');
SET IDENTITY_INSERT Account OFF;

SET IDENTITY_INSERT Floor ON;
INSERT INTO Floor (id, building_id, name, floor_level, capacity, manager_id) VALUES 
(3, 1, 'Floor 1', 1, 100, 20);
SET IDENTITY_INSERT Floor OFF;

SET IDENTITY_INSERT ParkingZone ON;
INSERT INTO ParkingZone (id, floor_id, name, vehicle_type_id, capacity) VALUES 
(2, 3, 'Zone A', 1, 50);
SET IDENTITY_INSERT ParkingZone OFF;

SET IDENTITY_INSERT Slot ON;
INSERT INTO Slot (id, zone_id, name, status) VALUES 
(1, 2, 'A1', 'EMPTY'),
(2, 2, 'A2', 'EMPTY');
SET IDENTITY_INSERT Slot OFF;

SET IDENTITY_INSERT PricingPolicy ON;
INSERT INTO PricingPolicy (id, name, vehicle_type_id, status) VALUES 
(1, 'Test Policy', 1, 'ACTIVE'),
(2, 'E2E Daily Pass', 1, 'ACTIVE');
SET IDENTITY_INSERT PricingPolicy OFF;

SET IDENTITY_INSERT Booking ON;
INSERT INTO Booking (id, account_id, building_id, cancelled_at, created_at, end_time, license_plate, start_time, status, vehicle_type_id) VALUES 
(1, 20, 1, NULL, '2026-06-01 11:25:59.1215587', '2026-06-01 13:25:59.0000000', '29A-12345', '2026-06-01 11:25:59.0000000', 'CONFIRMED', 1),
(2, 20, 1, '2026-06-01 11:31:09.3183319', '2026-06-01 11:26:05.8255399', '2026-06-01 13:26:05.0000000', '29A-67890', '2026-06-01 11:26:05.0000000', 'CANCELLED', 1),
(3, 20, 1, '2026-06-01 11:31:36.0954301', '2026-06-01 11:31:21.2393010', '2026-06-01 14:00:00.0000000', '29A-E2E99', '2026-06-01 12:00:00.0000000', 'CANCELLED', 1);
SET IDENTITY_INSERT Booking OFF;
GO
