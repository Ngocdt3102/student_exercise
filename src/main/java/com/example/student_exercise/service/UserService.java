package com.example.student_exercise.service;

import com.example.student_exercise.model.dto.user.UserCreateDTO;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;
import com.example.student_exercise.model.dto.user.UserUpdateDTO;

import java.util.UUID;

public interface UserService {
    
    // 1. Tạo mới tài khoản chung (Admin, Teacher, Staff)
    UserDetailResponseDTO createAdminOrTeacher(UserCreateDTO dto);

    // 2. Lấy thông tin chi tiết tài khoản theo ID
    UserDetailResponseDTO getUserById(UUID id);

    // 3. Cập nhật thông tin / Phân quyền / Khóa tài khoản
    UserDetailResponseDTO updateUser(UUID id, UserUpdateDTO dto);
}