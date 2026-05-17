package com.example.student_exercise.controller;

import com.example.student_exercise.model.dto.user.UserCreateDTO;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;
import com.example.student_exercise.model.dto.user.UserUpdateDTO;
import com.example.student_exercise.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * API tạo tài khoản nhân sự hệ thống (Admin, Teacher, Staff).
     * Mật khẩu và vai trò (role) được chỉ định chủ động bởi Quản trị viên cấp cao.
     */
    @PostMapping
    public ResponseEntity<UserDetailResponseDTO> createAccount(@Valid @RequestBody UserCreateDTO dto) {
        UserDetailResponseDTO response = userService.createAdminOrTeacher(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * API xem chi tiết thông tin tài khoản hệ thống theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable UUID id) {
        UserDetailResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * API điều chỉnh thông tin tài khoản chung (Thay đổi Email, Số điện thoại, Phân lại quyền lực hoặc Khóa/Mở tài khoản).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDetailResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO dto) {
        UserDetailResponseDTO response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }
}