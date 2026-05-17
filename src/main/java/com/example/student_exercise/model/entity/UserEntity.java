package com.example.student_exercise.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Tự động sinh mã UUID từ phía Java ứng dụng
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "is_active")
    private Boolean isActive; // Hibernate tự động map kiểu Boolean (true/false) thành kiểu BIT (1/0) trong SQL Server

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // --- Các trường Auditing (Lưu vết dữ liệu) ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    // --- Các trường Soft Delete (Xóa mềm) ---
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    // Thiết lập quan hệ 1-1 đảo chiều sang bảng Student
    // cascade = CascadeType.ALL để khi thao tác (thêm/sửa/xóa) User thì Student cũng tự động thao tác theo
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentEntity student;

    // Tự động gán thời gian khởi tạo trước khi lưu xuống database
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true; // Mặc định tài khoản hoạt động
        }
        if (this.role == null) {
            this.role = "student"; // Mặc định quyền là sinh viên
        }
    }

    // Tự động cập nhật thời gian sửa đổi trước khi cập nhật vào database
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}