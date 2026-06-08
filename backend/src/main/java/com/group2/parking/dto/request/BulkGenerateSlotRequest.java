/* Lớp DTO chứa dữ liệu đầu vào từ client khi yêu cầu tạo hàng loạt ô đỗ xe.
   Được dùng trong API POST /api/slots/bulk-generate. */

package com.group2.parking.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkGenerateSlotRequest {

    // ID của khu vực (zone) cần tạo slot bên trong
    private Integer zoneId;

    // Tiền tố cho tên ô đỗ, ví dụ "A1-" → tạo ra A1-01, A1-02, ...
    private String prefix;

    // Số lượng ô đỗ cần tạo trong lần này
    private Integer count;

    // Số thứ tự bắt đầu đánh (mặc định 1 nếu không truyền)
    private Integer startFrom;
}
