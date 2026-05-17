package com.example.student_exercise.controller;

import com.example.student_exercise.model.dto.auth.ChangePasswordRequestDTO;
import com.example.student_exercise.model.dto.common.PageResponseDTO;
import com.example.student_exercise.model.dto.student.StudentCreateDTO;
import com.example.student_exercise.model.dto.student.StudentDeleteRequestDTO;
import com.example.student_exercise.model.dto.student.StudentDetailResponseDTO;
import com.example.student_exercise.model.dto.student.StudentUpdateDTO;
import com.example.student_exercise.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // =========================================================================
    // VAI TRÒ: QUẢN TRỊ VIÊN (ADMIN / PHÒNG ĐÀO TẠO)
    // =========================================================================

    /**
     * API thêm mới sinh viên và tự động cấp phát tài khoản ngầm hệ thống.
     * Username mặc định là Mã sinh viên, Mật khẩu mặc định là Ngày sinh (ddMMyyyy).
     */
    @PostMapping
    public ResponseEntity<StudentDetailResponseDTO> createStudent(@Valid @RequestBody StudentCreateDTO dto) {
        StudentDetailResponseDTO response = studentService.createStudentAndAccount(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * API lấy danh sách toàn bộ sinh viên đang hoạt động trong hệ thống (chưa bị xóa mềm).
     */
    // [ADMIN/STUDENT] Lấy danh sách toàn bộ sinh viên có áp dụng Phân trang và Sắp xếp
    @GetMapping
    public ResponseEntity<PageResponseDTO<StudentDetailResponseDTO>> getAllStudents(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "studentCode", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageResponseDTO<StudentDetailResponseDTO> response = studentService.getAllActiveStudents(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    /**
     * API xem hồ sơ chi tiết của một sinh viên cụ thể theo ID tài khoản.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<StudentDetailResponseDTO> getStudentById(@PathVariable UUID userId) {
        StudentDetailResponseDTO response = studentService.getStudentById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * API dành riêng cho Admin để cập nhật toàn diện hồ sơ học vụ (GPA, Trạng thái, Lớp, Họ tên...).
     */
    @PutMapping("/admin/{userId}")
    public ResponseEntity<StudentDetailResponseDTO> updateStudentByAdmin(
            @PathVariable UUID userId,
            @Valid @RequestBody StudentUpdateDTO dto) {
        StudentDetailResponseDTO response = studentService.updateStudentByAdmin(userId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * API xóa mềm sinh viên khỏi hệ thống (lưu vết ngày xóa và tự động khóa tài khoản).
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> softDeleteStudent(
            @PathVariable UUID userId,
            @Valid @RequestBody StudentDeleteRequestDTO dto) {
        studentService.softDeleteStudent(userId, dto);
        return ResponseEntity.ok("Đã xóa mềm hồ sơ sinh viên và khóa tài khoản thành công!");
    }

    // =========================================================================
    // VAI TRÒ: SINH VIÊN (STUDENT)
    // =========================================================================

    /**
     * API cho phép sinh viên tự cập nhật thông tin cá nhân cơ bản (Địa chỉ, Quê quán, SĐT, Avatar).
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<StudentDetailResponseDTO> updateOwnProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody StudentUpdateDTO dto) {
        StudentDetailResponseDTO response = studentService.updateOwnProfile(userId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * API cho phép sinh viên thực hiện đổi mật khẩu tài khoản (yêu cầu mật khẩu cũ và xác nhận mật khẩu mới).
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequestDTO dto) {
        studentService.changePassword(userId, dto);
        return ResponseEntity.ok("Thay đổi mật khẩu tài khoản thành công!");
    }

    /**
     * API cho phép sinh viên tra cứu và xem danh sách các sinh viên khác học cùng lớp với mình.
     */
    @GetMapping("/class/{className}")
    public ResponseEntity<List<StudentDetailResponseDTO>> getStudentsByClass(@PathVariable String className) {
        List<StudentDetailResponseDTO> response = studentService.getStudentsByClassName(className);
        return ResponseEntity.ok(response);
    }
}