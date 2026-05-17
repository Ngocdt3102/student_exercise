package com.example.student_exercise.controller;

import com.example.student_exercise.model.dto.auth.*;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;
import com.example.student_exercise.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 1. ĐĂNG NHẬP HỆ THỐNG
     * Trả về JWT Token để người dùng sử dụng cho các request sau này.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. ĐĂNG KÝ TÀI KHOẢN MỚI
     * Dành cho học viên/khách vãng lai tự đăng ký (Mặc định được gán quyền 'student').
     */
    @PostMapping("/register")
    public ResponseEntity<UserDetailResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        UserDetailResponseDTO response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 3. QUÊN MẬT KHẨU
     * Nhận email từ người dùng và gửi mã OTP (mô phỏng in ra màn hình Console).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Mã xác nhận OTP khôi phục mật khẩu đã được gửi đi. Vui lòng kiểm tra email hệ thống!");
    }

    /**
     * 4. ĐẶT LẠI MẬT KHẨU MỚI
     * Nhận mã OTP và mật khẩu mới để tiến hành cập nhật lại mật khẩu cho tài khoản.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công! Bạn có thể dùng mật khẩu mới để đăng nhập.");
    }

    /**
     * 5. ĐỔI MẬT KHẨU CHỦ ĐỘNG
     * Dành cho người dùng đang trong trạng thái đăng nhập, muốn đổi mật khẩu mới (cần nhập mật khẩu cũ).
     */
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequestDTO dto) {
        authService.changePassword(userId, dto);
        return ResponseEntity.ok("Đổi mật khẩu tài khoản thành công!");
    }
}