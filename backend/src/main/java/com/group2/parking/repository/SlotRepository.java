/* Interface Repository cung cấp các phương thức truy vấn và thao tác dữ liệu
   cho bảng Slot trong cơ sở dữ liệu.
   Spring Data JPA tự động sinh câu SQL dựa trên tên phương thức. */

package com.group2.parking.repository;

import com.group2.parking.entity.Slot;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Integer> {

    // Kiểm tra xem khu vực (zone) có chứa ít nhất một ô đỗ không
    boolean existsByZoneId(Integer zoneId);

    // Lấy toàn bộ danh sách ô đỗ thuộc một khu vực
    List<Slot> findByZoneId(Integer zoneId);

    // Đếm số ô đỗ theo khu vực và trạng thái cụ thể (EMPTY / OCCUPIED / MAINTENANCE)
    long countByZoneIdAndStatus(Integer zoneId, String status);

    // Đếm tổng số ô đỗ trong một khu vực
    long countByZoneId(Integer zoneId);

    // Xóa toàn bộ ô đỗ trong một khu vực (dùng khi xóa zone — cascade delete)
    @Transactional
    void deleteByZoneId(Integer zoneId);

    // Đếm số ô đỗ theo tầng và trạng thái (dùng JPQL join qua bảng ParkingZone)
    @Query("SELECT COUNT(s) FROM Slot s JOIN ParkingZone z ON s.zoneId = z.id WHERE z.floor.id = :floorId AND s.status = :status")
    long countByFloorIdAndStatus(@Param("floorId") Integer floorId, @Param("status") String status);

    // Đếm tổng số ô đỗ trong một tầng (dùng JPQL join qua bảng ParkingZone)
    @Query("SELECT COUNT(s) FROM Slot s JOIN ParkingZone z ON s.zoneId = z.id WHERE z.floor.id = :floorId")
    long countByFloorId(@Param("floorId") Integer floorId);

    // Đếm số ô đỗ đang hoạt động (không phải MAINTENANCE) theo tòa nhà và loại xe
    // Dùng Native Query để join qua nhiều bảng: Slot → Zone → Floor → Building
    @Query(value = """
        SELECT COUNT(s.id)
        FROM Slot s
        JOIN ParkingZone z ON s.zone_id = z.id
        JOIN Floor f ON z.floor_id = f.id
        WHERE f.building_id = :buildingId
          AND z.vehicle_type_id = :vehicleTypeId
          AND s.status <> 'MAINTENANCE'
    """, nativeQuery = true)
    long countUsableSlots(
            @Param("buildingId") Integer buildingId,
            @Param("vehicleTypeId") Integer vehicleTypeId
    );
}