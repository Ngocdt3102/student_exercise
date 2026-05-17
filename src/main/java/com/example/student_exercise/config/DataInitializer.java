package com.example.student_exercise.config;

import com.example.student_exercise.model.entity.UserEntity;
import com.example.student_exercise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem tên đăng nhập "admin" đã tồn tại dưới DB chưa
        if (!userRepository.existsByUsername("admin")) {
            
            // Khởi tạo tài khoản Admin tối cao mặc định cho hệ thống
            UserEntity superAdmin = UserEntity.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // Mật khẩu mặc định
                    .email("admin@system.com")
                    .phoneNumber("0123456789")
                    .role("admin") // Gán thẳng quyền admin ở tầng lõi
                    .isActive(true)
                    .build();

            userRepository.save(superAdmin);
            
            System.out.println("\n=========================================================");
            System.out.println("SYSTEM NOTICE: ĐÃ KHỞI TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH!");
            System.out.println("Tên đăng nhập: admin");
            System.out.println("Mật khẩu: admin123");
            System.out.println("Vui lòng đổi mật khẩu ngay sau lần đăng nhập đầu tiên.");
            System.out.println("=========================================================\n");
        }
    }
}