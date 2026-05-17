package com.example.student_exercise.model.dto.student;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StudentUpdateDTO {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String avatarUrl;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;

    private String gender;
    private String hometown;
    private String address;
    private String faculty;
    private String major;
    private String className;
    
    private Integer expectedGraduationYear;
    private Integer actualGraduationYear;

    @DecimalMin(value = "0.00", message = "Điểm GPA không được nhỏ hơn 0.00")
    @DecimalMax(value = "4.00", message = "Điểm GPA không được lớn hơn 4.00")
    private BigDecimal cumulativeGpa;

    private String academicStatus;
    
    @NotNull(message = "Trạng thái tài khoản không được để trống")
    private Boolean isActive; // Cho phép Admin khóa/mở tài khoản sinh viên này
}