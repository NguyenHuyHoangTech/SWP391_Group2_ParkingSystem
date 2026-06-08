/* Lớp DTO trả về kết quả sau khi thực hiện tạo hàng loạt ô đỗ xe.
   Cung cấp số lượng tạo thành công, số bị bỏ qua và danh sách tên cụ thể. */

package com.group2.parking.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkGenerateResult {

    // Số ô đỗ được tạo thành công trong lần này
    private int created;

    // Số ô đỗ bị bỏ qua do trùng tên với slot đã tồn tại
    private int skipped;

    // Danh sách tên cụ thể của các ô đã tạo thành công, ví dụ ["A1-01", "A1-02"]
    private List<String> createdNames;

    // Danh sách tên bị bỏ qua (đã tồn tại trong zone), ví dụ ["A1-03"]
    private List<String> skippedNames;
}
