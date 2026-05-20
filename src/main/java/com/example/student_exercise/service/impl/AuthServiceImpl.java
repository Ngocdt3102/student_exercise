package com.example.student_exercise.service.impl;

import com.example.student_exercise.model.dto.auth.*;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;
import com.example.student_exercise.model.entity.UserEntity;
import com.example.student_exercise.repository.UserRepository;
import com.example.student_exercise.security.JwtUtils;
import com.example.student_exercise.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================================================
    // 1. ĐĂNG NHẬP
    // =========================================================================
    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserEntity user = userRepository.findByUsernameAndDeletedAtIsNull(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại hoặc đã bị xóa mềm!"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản này hiện đang bị khóa!");
        }

        return AuthResponseDTO.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    // =========================================================================
    // 2. ĐĂNG KÝ TÀI KHOẢN (ĐÃ TÍCH HỢP BẢO MẬT CHẶN HACKER)
    // =========================================================================
    @Override
    @Transactional
    public UserDetailResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập này đã tồn tại!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // BẢO MẬT: Lọc quyền truy cập (Role Filtering)
        // Bắt buộc ép quyền về "student" bất chấp dữ liệu Client gửi lên là gì
        String requestedRole = dto.getRole() != null ? dto.getRole().toLowerCase() : "";
        if (!requestedRole.equals("student")) {
            throw new RuntimeException("Cảnh báo bảo mật: Bạn không có quyền đăng ký tài khoản cấp cao! Vui lòng liên hệ Admin.");
        }

        UserEntity newUser = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .role("student") // Gán cứng an toàn tuyệt đối ở tầng lõi
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(newUser);
        
        return UserDetailResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    // =========================================================================
    // 3. QUÊN MẬT KHẨU (TẠO MÃ OTP VÀ MÔ PHỎNG GỬI EMAIL)
    // =========================================================================
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        UserEntity user = userRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nào liên kết với Email này!"));

        // Sinh mã ngẫu nhiên OTP gồm 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Cấu hình thời hạn: Hết hạn sau 5 phút tính từ thời điểm hiện tại
        user.setResetToken(otp);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // MÔ PHỎNG GỬI EMAIL: In thẳng mã OTP ra ô cửa sổ đen Console của ứng dụng
        System.out.println("\n=========================================================");
        System.out.println("SYSTEM EMAIL MOCK - GỬI MÃ OTP KHÔI PHỤC MẬT KHẨU");
        System.out.println("Gửi đến Email: " + dto.getEmail());
        System.out.println("Mã xác nhận OTP của bạn là: " + otp);
        System.out.println("Mã này có hiệu lực trong vòng 5 phút (Đến: " + user.getTokenExpiration() + ")");
        System.out.println("=========================================================\n");
    }

    // =========================================================================
    // 4. ĐẶT LẠI MẬT KHẨU MỚI (KIỂM TRA OTP)
    // =========================================================================
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        UserEntity user = userRepository.findByResetTokenAndDeletedAtIsNull(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Mã xác nhận OTP không chính xác hoặc không tồn tại!"));

        // Kiểm tra thời hạn hiệu lực của mã OTP
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP này đã hết thời gian hiệu lực (Quá 5 phút)! Vui lòng yêu cầu cấp lại mã mới.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không trùng khớp!");
        }

        // Đổi mật khẩu, xóa vết OTP để tránh dùng lại lần 2 (Replay Attack)
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);
    }

    // =========================================================================
    // 5. ĐỔI MẬT KHẨU CHỦ ĐỘNG
    // =========================================================================
    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequestDTO dto) {
        // BƯỚC FIX CẢNH BÁO: Kiểm tra Null trước khi gọi Database
        if (userId == null) {
            throw new IllegalArgumentException("Mã người dùng (userId) không được để trống!");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Giải mã và so sánh mật khẩu cũ do Client truyền lên với DB
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không trùng khớp!");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}