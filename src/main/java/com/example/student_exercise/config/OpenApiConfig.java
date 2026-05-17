package com.example.student_exercise.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "HỆ THỐNG QUẢN LÝ SINH VIÊN API",
                version = "1.0",
                description = "Tài liệu tích hợp API dành cho đội ngũ Frontend (ReactJS/VueJS) và Mobile.",
                contact = @Contact(name = "Đặng Thọ Ngọc", email = "ngoc@example.com")
        ),
        // Áp dụng Nút ổ khóa bảo mật cho toàn bộ hệ thống
        security = @SecurityRequirement(name = "bearerAuth") 
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Vui lòng nhập JWT Token được cấp sau khi gọi API Đăng nhập (Login). Bỏ đi chữ 'Bearer ' khi nhập vào ô này.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP, // LỖI ĐÃ ĐƯỢC SỬA Ở DÒNG NÀY
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // File này dùng các Annotation để cấu hình giao diện Swagger
}