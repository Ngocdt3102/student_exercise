package com.example.student_exercise.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken; // Chuỗi JWT để xác thực
    private String tokenType;   // Thường là "Bearer"
    private UUID userId;        // Trả về ID để Client gọi các API profile
    private String username;
    private String role;        // 'admin' hoặc 'student'
}