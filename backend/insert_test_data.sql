-- Clean up existing data to avoid conflicts if necessary, or just insert new test data
-- Disabling foreign key checks or just deleting in correct order
DELETE FROM Slot;
DELETE FROM ParkingZone;
DELETE FROM Floor;
DELETE FROM VehicleType;
DELETE FROM ParkingBuilding;
DELETE FROM Account;

SET IDENTITY_INSERT VehicleType ON;
INSERT INTO VehicleType (id, name, description) VALUES 
(1, 'Car', 'Standard car'),
(2, 'Motorbike', 'Standard motorbike');
SET IDENTITY_INSERT VehicleType OFF;

SET IDENTITY_INSERT ParkingBuilding ON;
INSERT INTO ParkingBuilding (id, name, address, status) VALUES 
(1, 'Building A', '123 Main St', 'OPEN'),
(2, 'Building B', '456 Side St', 'OPEN');
SET IDENTITY_INSERT ParkingBuilding OFF;

SET IDENTITY_INSERT Floor ON;
INSERT INTO Floor (id, building_id, name, floor_level, capacity) VALUES 
(1, 1, 'Floor 1', 1, 100),
(2, 1, 'Floor 2', 2, 100);
SET IDENTITY_INSERT Floor OFF;

SET IDENTITY_INSERT ParkingZone ON;
INSERT INTO ParkingZone (id, floor_id, name, vehicle_type_id, capacity) VALUES 
(1, 1, 'Zone A1', 1, 50),
(2, 2, 'Zone B1', 2, 50);
SET IDENTITY_INSERT ParkingZone OFF;

SET IDENTITY_INSERT Slot ON;
INSERT INTO Slot (id, zone_id, name, status) VALUES 
(1, 1, 'A1-01', 'EMPTY'),
(2, 1, 'A1-02', 'EMPTY'),
(3, 2, 'B1-01', 'EMPTY'),
(4, 2, 'B1-02', 'EMPTY');
SET IDENTITY_INSERT Slot OFF;

SET IDENTITY_INSERT Account ON;
INSERT INTO Account (id, username, password, email, phone, role, status, building_id) VALUES 
(1, 'admin1', '123456', 'admin@test.com', '0000000000', 'ADMIN', 'ACTIVE', NULL),
(20, 'manager1', '123456', 'manager@test.com', '0123456789', 'MANAGER', 'ACTIVE', 1),
(21, 'staff1', '123456', 'staff@test.com', '0123456788', 'STAFF', 'ACTIVE', 1),
(22, 'customer1', '123456', 'customer@test.com', '0123456787', 'USER', 'ACTIVE', NULL);
SET IDENTITY_INSERT Account OFF;
