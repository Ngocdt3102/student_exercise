package com.example.student_exercise.model.dto.student;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentCreateDTO {

    @NotBlank(message = "Mã số sinh viên không được để trống")
    @Size(max = 20, message = "Mã số sinh viên không quá 20 ký tự")
    private String studentCode;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không quá 100 ký tự")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải từ 10 đến 15 chữ số")
    private String phoneNumber;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @NotBlank(message = "Số CCCD không được để trống")
    @Size(min = 12, max = 12, message = "Số CCCD phải chính xác 12 chữ số")
    @Pattern(regexp = "^[0-9]+$", message = "Số CCCD chỉ được chứa các chữ số")
    private String nationalId;

    private String hometown;
    private String address;
    private String faculty;
    private String major;
    private String className;

    @Min(value = 1900, message = "Năm nhập học không hợp lệ")
    private Integer enrollmentYear;

    @Min(value = 1900, message = "Năm tốt nghiệp dự kiến không hợp lệ")
    private Integer expectedGraduationYear;
}