package com.example.student_exercise.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;       // Danh sách dữ liệu của trang hiện tại (VD: danh sách sinh viên)
    private int pageNo;            // Số trang hiện tại (Bắt đầu từ 0)
    private int pageSize;          // Số lượng phần tử trên một trang
    private long totalElements;    // Tổng số bản ghi tồn tại trong Database
    private int totalPages;        // Tổng số trang tính được
    private boolean isLast;        // Đây có phải là trang cuối cùng không?
}