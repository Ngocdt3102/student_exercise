package com.example.student_exercise.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDTO {
    private LocalDateTime timestamp; // Thời gian xảy ra lỗi
    private int status;              // Mã HTTP Status (400, 401, 403, 404, 500...)
    private String error;            // Tên loại lỗi (Bad Request, Unauthorized...)
    private Object message;          // Câu thông báo lỗi chi tiết (Có thể là String hoặc Map)
}