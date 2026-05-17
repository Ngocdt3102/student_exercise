package com.example.student_exercise.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO {
    
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLogin;

    // --- Lịch sử thao tác hệ thống (Auditing) ---
    private LocalDateTime createdAt;
    private String createdByUsername; // Tên hiển thị của người tạo tài khoản này
    private LocalDateTime updatedAt;
    private String updatedByUsername; // Tên hiển thị của người cập nhật tài khoản gần nhất
}