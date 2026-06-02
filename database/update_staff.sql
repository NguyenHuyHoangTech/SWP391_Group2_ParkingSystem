ALTER TABLE Floor
ADD manager_id INT NULL;
GO

ALTER TABLE Floor
ADD CONSTRAINT FK_Floor_Manager
FOREIGN KEY (manager_id) REFERENCES Account(id);
GO

INSERT INTO ParkingBuilding (name, address, status)
VALUES ('Main Parking Building', 'FPT University', 'OPEN');
GO

INSERT INTO Account (username, password, email, phone, role, building_id, status)
VALUES ('manager1', '123456', 'manager1@example.com', '0900000001', 'MANAGER', 1, 'ACTIVE');
GO

INSERT INTO Floor (building_id, manager_id, name, floor_level, capacity)
VALUES (1, 1, 'Floor 1', 1, 100);
GO

SELECT * FROM Account;
SELECT * FROM ParkingBuilding;
SELECT * FROM Floor;

SELECT id, username, role FROM Account WHERE role = 'MANAGER';
SELECT id, name, floor_level, manager_id FROM Floor;
GO
