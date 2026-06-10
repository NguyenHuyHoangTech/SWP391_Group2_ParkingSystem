/* Lớp Entity đại diện cho một ô đỗ xe trong cơ sở dữ liệu.
   Mỗi ô đỗ thuộc về một khu vực (zone), có tên định danh và trạng thái hiện tại. */

package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Slot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    // Khóa chính, tự động sinh (identity)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Khóa ngoại liên kết đến khu vực (ParkingZone) mà ô đỗ này thuộc về
    @Column(name = "zone_id", nullable = false)
    private Integer zoneId;

    // Tên hiển thị của ô đỗ, ví dụ: "A1-01", "Zone B1-03"
    @Column(nullable = false)
    private String name;

    // Trạng thái hiện tại của ô đỗ:
    //   EMPTY       - ô trống, sẵn sàng đón xe
    //   OCCUPIED    - đang có xe đậu
    //   MAINTENANCE - đang bảo trì, không sử dụng được
    @Column(nullable = false)
    private String status;
}