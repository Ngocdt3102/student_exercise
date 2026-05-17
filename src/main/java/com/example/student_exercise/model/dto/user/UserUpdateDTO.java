package com.example.student_exercise.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateDTO {

    // Không cho phép đổi Username vì đây là định danh gốc của hệ thống
    // Không chứa Password vì đổi mật khẩu đã có luồng API và DTO riêng (ChangePasswordRequestDTO)

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String avatarUrl;

    // Cho phép Admin phân quyền lại (VD: nâng cấp từ "teacher" lên "admin")
    private String role; 
    
    // Dùng kiểu đối tượng Boolean (thay vì boolean nguyên thủy) để cho phép giá trị null
    // Cho phép Admin khóa (false) hoặc mở khóa (true) tài khoản
    private Boolean isActive; 
}