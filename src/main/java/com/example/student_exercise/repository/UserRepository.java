package com.example.student_exercise.repository;

import com.example.student_exercise.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    // =========================================================================
    // 1. TRUY VẤN XÁC THỰC VÀ ĐĂNG NHẬP (Lọc bỏ các tài khoản đã bị xóa mềm)
    // =========================================================================
    
    // Tìm kiếm người dùng bằng Username và tài khoản đó chưa bị xóa mềm
    Optional<UserEntity> findByUsernameAndDeletedAtIsNull(String username);

    // Tìm kiếm người dùng bằng Email và tài khoản đó chưa bị xóa mềm
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Tìm tài khoản dựa trên mã OTP khôi phục mật khẩu và hồ sơ chưa bị xóa mềm.
     * Hàm này trực tiếp phục vụ cho luồng xử lý Đặt lại mật khẩu (Reset Password).
     */
    Optional<UserEntity> findByResetTokenAndDeletedAtIsNull(String token);


    // =========================================================================
    // 2. TRUY VẤN PHỤC VỤ KIỂM TRA TRÙNG LẶP (VALIDATION) KHI ĐĂNG KÝ/THÊM MỚI
    // =========================================================================
    
    // Kiểm tra tên đăng nhập đã tồn tại trong hệ thống chưa (kể cả trong thùng rác)
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại trong hệ thống chưa
    boolean existsByEmail(String email);


    // =========================================================================
    // 3. QUẢN LÝ DỮ LIỆU HOẠT ĐỘNG VÀ THÙNG RÁC
    // =========================================================================
    
    // Lấy toàn bộ danh sách người dùng chưa bị xóa mềm
    List<UserEntity> findAllByDeletedAtIsNull();

    // Lấy danh sách người dùng đã bị xóa mềm (Phục vụ chức năng Thùng rác / Khôi phục)
    List<UserEntity> findAllByDeletedAtIsNotNull();
}