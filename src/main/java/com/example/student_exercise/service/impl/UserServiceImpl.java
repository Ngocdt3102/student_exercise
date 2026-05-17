package com.example.student_exercise.service.impl;

import com.example.student_exercise.model.dto.user.UserCreateDTO;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;
import com.example.student_exercise.model.dto.user.UserUpdateDTO;
import com.example.student_exercise.model.entity.UserEntity;
import com.example.student_exercise.repository.UserRepository;
import com.example.student_exercise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================================================
    // 1. TẠO TÀI KHOẢN MỚI
    // =========================================================================
    @Override
    @Transactional
    public UserDetailResponseDTO createAdminOrTeacher(UserCreateDTO dto) {
        
        // Kiểm tra tính duy nhất
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập này đã tồn tại trong hệ thống!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng cho một tài khoản khác!");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        UserEntity newUser = UserEntity.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole().toLowerCase()) 
                .isActive(true) 
                .build();

        UserEntity savedUser = userRepository.save(newUser);

        return mapToUserDetailResponse(savedUser);
    }

    // =========================================================================
    // 2. XEM CHI TIẾT TÀI KHOẢN
    // =========================================================================
    @Override
    public UserDetailResponseDTO getUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID này!"));
        
        return mapToUserDetailResponse(user);
    }

    // =========================================================================
    // 3. CẬP NHẬT TÀI KHOẢN (Admin dùng để đổi quyền hoặc khóa nick)
    // =========================================================================
    @Override
    @Transactional
    public UserDetailResponseDTO updateUser(UUID id, UserUpdateDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        // Nếu Admin muốn đổi Email của người này, phải check xem email mới có trùng ai không
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email mới này đã được đăng ký bởi người khác!");
            }
            user.setEmail(dto.getEmail());
        }

        // Cập nhật các trường còn lại nếu có truyền dữ liệu lên
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getRole() != null) user.setRole(dto.getRole().toLowerCase());
        if (dto.getIsActive() != null) user.setIsActive(dto.getIsActive()); // Khóa hoặc mở tài khoản

        return mapToUserDetailResponse(userRepository.save(user));
    }

    // =========================================================================
    // HÀM PHỤ TRỢ (HELPER METHOD)
    // =========================================================================
    private UserDetailResponseDTO mapToUserDetailResponse(UserEntity user) {
        return UserDetailResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}