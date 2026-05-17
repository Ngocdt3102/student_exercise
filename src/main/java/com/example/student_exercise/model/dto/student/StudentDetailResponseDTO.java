package com.example.student_exercise.model.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponseDTO {
    
    // ==========================================
    // 1. DỮ LIỆU TÀI KHOẢN (Bảng Users)
    // ==========================================
    private UUID userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLogin;

    // ==========================================
    // 2. DỮ LIỆU HỒ SƠ HỌC VỤ (Bảng Students)
    // ==========================================
    private String studentCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationalId;
    private String hometown;
    private String address;
    private String faculty;
    private String major;
    private String className;
    private Integer enrollmentYear;
    private Integer expectedGraduationYear;
    private Integer actualGraduationYear;
    private BigDecimal cumulativeGpa;
    private String academicStatus;

    // ==========================================
    // 3. LỊCH SỬ THAO TÁC TÀI KHOẢN (User Auditing)
    // ==========================================
    private LocalDateTime userCreatedAt;
    private String userCreatedByUsername; // Tiện hiển thị tên người tạo thay vì mã UUID khó hiểu
    private LocalDateTime userUpdatedAt;
    private String userUpdatedByUsername;

    // ==========================================
    // 4. LỊCH SỬ THAO TÁC HỌC VỤ (Student Auditing)
    // ==========================================
    private LocalDateTime studentCreatedAt;
    private String studentCreatedByUsername; 
    private LocalDateTime studentUpdatedAt;
    private String studentUpdatedByUsername;
    
    // (Lưu ý: Không trả về trường deleted_at và deleted_by ở đây 
    // vì API này thường chỉ dùng để xem thông tin sinh viên ĐANG HOẠT ĐỘNG. 
    // Sinh viên đã bị xóa mềm sẽ được lấy ra bằng một API riêng biệt cho mục đích khôi phục hoặc xem thùng rác).
}