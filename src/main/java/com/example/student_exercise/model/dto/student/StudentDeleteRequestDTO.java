package com.example.student_exercise.model.dto.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentDeleteRequestDTO {

    @NotBlank(message = "Vui lòng cung cấp lý do xóa hồ sơ sinh viên này")
    private String deletionReason; // Ví dụ: "Sinh viên thôi học", "Chuyển trường", "Nhập sai thông tin"
}