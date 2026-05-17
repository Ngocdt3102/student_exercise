package com.example.student_exercise.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data // Tự động sinh Getter, Setter, toString (của Lombok)
public class RegisterRequestDTO {

    // --- Thông tin User ---
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    // --- Thông tin Student bắt buộc ---
    @NotBlank(message = "Mã số sinh viên không được để trống")
    private String studentCode;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Size(min = 12, max = 12, message = "Số CCCD phải có đúng 12 ký tự")
    private String nationalId;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;
}