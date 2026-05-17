package com.example.student_exercise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Kích hoạt cơ chế JPA Auditing toàn hệ thống
public class JpaConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new AuditorAwareImpl();
    }
}

/**
 * Lớp thực thi (Implementation) nhiệm vụ cung cấp định danh người thao tác hiện tại
 * để tự động điền vào các trường lưu vết dữ liệu hệ thống (created_by, updated_by).
 */
class AuditorAwareImpl implements AuditorAware<UUID> {
    
    @Override
    public Optional<UUID> getCurrentAuditor() {
        // GIAI ĐOẠN PHÁT TRIỂN / THỬ NGHIỆM: Tạm thời cấu hình trả về một mã UUID cố định 
        // đại diện cho Hệ thống hoặc tài khoản Quản trị viên mặc định (System/Admin).
        // Sau này khi chạy ứng dụng thực tế, logic này sẽ được thay thế bằng việc trích xuất
        // UUID trực tiếp từ SecurityContextHolder của hệ thống xác thực JWT.
        return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }
}