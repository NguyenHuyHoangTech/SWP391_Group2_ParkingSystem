USE ParkingManagementSystem;

DECLARE @buildingId INT;
DECLARE @floorId INT;
DECLARE @zoneId INT;
DECLARE @slot1 INT, @slot2 INT, @slot3 INT;
DECLARE @card1 INT, @card2 INT, @card3 INT;
DECLARE @session1 INT, @session2 INT, @session3 INT;

INSERT INTO ParkingBuilding (name, address, status)
VALUES ('Test Building', 'FPT University', 'OPEN');
SET @buildingId = SCOPE_IDENTITY();

INSERT INTO Floor (building_id, name, floor_level, capacity)
VALUES (@buildingId, 'Test Floor 1', 1, 100);
SET @floorId = SCOPE_IDENTITY();

INSERT INTO ParkingZone (floor_id, name, vehicle_type_id, capacity)
VALUES (@floorId, 'Test Zone A', 1, 50);
SET @zoneId = SCOPE_IDENTITY();

INSERT INTO Slot (zone_id, name, status)
VALUES (@zoneId, 'T-A1', 'EMPTY');
SET @slot1 = SCOPE_IDENTITY();

INSERT INTO Slot (zone_id, name, status)
VALUES (@zoneId, 'T-A2', 'EMPTY');
SET @slot2 = SCOPE_IDENTITY();

INSERT INTO Slot (zone_id, name, status)
VALUES (@zoneId, 'T-A3', 'EMPTY');
SET @slot3 = SCOPE_IDENTITY();

INSERT INTO ParkingCard (card_code, status)
VALUES ('TEST-CARD-001', 'AVAILABLE');
SET @card1 = SCOPE_IDENTITY();

INSERT INTO ParkingCard (card_code, status)
VALUES ('TEST-CARD-002', 'AVAILABLE');
SET @card2 = SCOPE_IDENTITY();

INSERT INTO ParkingCard (card_code, status)
VALUES ('TEST-CARD-003', 'AVAILABLE');
SET @card3 = SCOPE_IDENTITY();

INSERT INTO ParkingSession
(account_id, building_id, slot_id, vehicle_type_id, card_id, license_plate, entry_gate, exit_gate, check_in_time, check_out_time, status)
VALUES
(NULL, @buildingId, @slot1, 1, @card1, '51A-11111', 'Gate A', 'Gate B', '2026-06-01 08:00:00', '2026-06-01 10:00:00', 'COMPLETED');
SET @session1 = SCOPE_IDENTITY();

INSERT INTO ParkingSession
(account_id, building_id, slot_id, vehicle_type_id, card_id, license_plate, entry_gate, exit_gate, check_in_time, check_out_time, status)
VALUES
(NULL, @buildingId, @slot2, 1, @card2, '51A-22222', 'Gate A', 'Gate B', '2026-06-01 09:30:00', '2026-06-01 12:00:00', 'COMPLETED');
SET @session2 = SCOPE_IDENTITY();

INSERT INTO ParkingSession
(account_id, building_id, slot_id, vehicle_type_id, card_id, license_plate, entry_gate, exit_gate, check_in_time, check_out_time, status)
VALUES
(NULL, @buildingId, @slot3, 1, @card3, '51A-33333', 'Gate A', 'Gate B', '2026-06-02 14:00:00', '2026-06-02 16:30:00', 'COMPLETED');
SET @session3 = SCOPE_IDENTITY();

INSERT INTO Payment
(session_id, monthly_ticket_id, amount, payment_method, status, created_at)
VALUES
(@session1, NULL, 20000, 'CASH', 'SUCCESS', '2026-06-01 10:05:00'),
(@session2, NULL, 30000, 'MOMO', 'SUCCESS', '2026-06-01 12:05:00'),
(@session3, NULL, 25000, 'VNPAY', 'SUCCESS', '2026-06-02 16:35:00');