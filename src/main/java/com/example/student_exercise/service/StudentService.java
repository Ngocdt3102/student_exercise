package com.example.student_exercise.service;

import com.example.student_exercise.model.dto.auth.ChangePasswordRequestDTO;
import com.example.student_exercise.model.dto.common.PageResponseDTO;
import com.example.student_exercise.model.dto.student.StudentCreateDTO;
import com.example.student_exercise.model.dto.student.StudentDeleteRequestDTO;
import com.example.student_exercise.model.dto.student.StudentDetailResponseDTO;
import com.example.student_exercise.model.dto.student.StudentUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    
    // =========================================================================
    // LUỒNG CỦA QUẢN TRỊ VIÊN (ADMIN)
    // =========================================================================
    StudentDetailResponseDTO createStudentAndAccount(StudentCreateDTO dto);
    
    PageResponseDTO<StudentDetailResponseDTO> getAllActiveStudents(int pageNo, int pageSize, String sortBy, String sortDir);
    
    StudentDetailResponseDTO getStudentById(UUID userId);
    
    StudentDetailResponseDTO updateStudentByAdmin(UUID userId, StudentUpdateDTO dto);
    
    void softDeleteStudent(UUID userId, StudentDeleteRequestDTO dto);

    // =========================================================================
    // LUỒNG CỦA SINH VIÊN (STUDENT)
    // =========================================================================
    List<StudentDetailResponseDTO> getStudentsByClassName(String className);
    
    StudentDetailResponseDTO updateOwnProfile(UUID userId, StudentUpdateDTO dto);
    
    void changePassword(UUID userId, ChangePasswordRequestDTO dto);
}