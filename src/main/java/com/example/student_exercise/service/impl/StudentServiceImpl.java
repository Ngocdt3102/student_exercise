package com.example.student_exercise.service.impl;

import com.example.student_exercise.model.dto.auth.ChangePasswordRequestDTO;
import com.example.student_exercise.model.dto.common.PageResponseDTO;
import com.example.student_exercise.model.dto.student.StudentCreateDTO;
import com.example.student_exercise.model.dto.student.StudentDeleteRequestDTO;
import com.example.student_exercise.model.dto.student.StudentDetailResponseDTO;
import com.example.student_exercise.model.dto.student.StudentUpdateDTO;
import com.example.student_exercise.model.entity.StudentEntity;
import com.example.student_exercise.model.entity.UserEntity;
import com.example.student_exercise.repository.StudentRepository;
import com.example.student_exercise.repository.UserRepository;
import com.example.student_exercise.service.StudentService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Dùng để băm mật khẩu

    // =========================================================================
    // 1. TẠO MỚI (Tự động sinh tài khoản)
    // =========================================================================
    @Override
    @Transactional // RẤT QUAN TRỌNG: Đảm bảo tính toàn vẹn dữ liệu
    public StudentDetailResponseDTO createStudentAndAccount(StudentCreateDTO dto) {
        
        // Kiểm tra các quy tắc nghiệp vụ (Business Validation)
        if (studentRepository.existsByStudentCode(dto.getStudentCode())) {
            throw new RuntimeException("Mã số sinh viên đã tồn tại trong hệ thống!");
        }
        if (studentRepository.existsByNationalId(dto.getNationalId())) {
            throw new RuntimeException("Số CCCD này đã được đăng ký cho một hồ sơ khác!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // TỰ ĐỘNG SINH MẬT KHẨU (Quy tắc: Định dạng Ngày sinh ddMMyyyy)
        String rawPassword = dto.getDateOfBirth().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Khởi tạo đối tượng UserEntity
        UserEntity newUser = UserEntity.builder()
                .username(dto.getStudentCode()) // Dùng MSSV làm Tên đăng nhập
                .password(encodedPassword)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .role("student")
                .isActive(true)
                .build();

        // Khởi tạo đối tượng StudentEntity
        StudentEntity newStudent = StudentEntity.builder()
                .studentCode(dto.getStudentCode())
                .fullName(dto.getFullName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .nationalId(dto.getNationalId())
                .hometown(dto.getHometown())
                .address(dto.getAddress())
                .faculty(dto.getFaculty())
                .major(dto.getMajor())
                .className(dto.getClassName())
                .enrollmentYear(dto.getEnrollmentYear())
                .expectedGraduationYear(dto.getExpectedGraduationYear())
                .build();

        // Móc nối quan hệ 1-1
        newStudent.setUser(newUser);   
        newUser.setStudent(newStudent); 

        // Lưu xuống Database (Lưu 1 được 2)
        UserEntity savedUser = userRepository.save(newUser);

        return mapToStudentDetailResponse(savedUser);
    }

    // =========================================================================
    // 2. LUỒNG NGHIỆP VỤ QUẢN TRỊ VIÊN (ADMIN)
    // =========================================================================
    @Override
    public PageResponseDTO<StudentDetailResponseDTO> getAllActiveStudents(int pageNo, int pageSize, String sortBy, String sortDir) {
        
        // 1. Khởi tạo đối tượng Sort dựa trên yêu cầu sắp xếp Tăng hay Giảm dần
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        // 2. Tạo đối tượng Pageable chứa thông tin trang và sắp xếp
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        // 3. Gọi Repository thực thi câu lệnh SQL phân trang
        org.springframework.data.domain.Page<StudentEntity> studentPage = studentRepository.findAllByDeletedAtIsNull(pageable);
        
        // 4. Chuyển đổi List<StudentEntity> bên trong Page thành List<StudentDetailResponseDTO>
        List<StudentDetailResponseDTO> content = studentPage.getContent().stream()
                .map(student -> mapToStudentDetailResponse(student.getUser()))
                .toList();
                
        // 5. Đóng gói toàn bộ thông tin vào PageResponseDTO để trả về
        return PageResponseDTO.<StudentDetailResponseDTO>builder()
                .content(content)
                .pageNo(studentPage.getNumber())
                .pageSize(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .isLast(studentPage.isLast())
                .build();
    }

    @Override
    public StudentDetailResponseDTO getStudentById(UUID userId) {
        StudentEntity student = studentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ sinh viên này!"));
        return mapToStudentDetailResponse(student.getUser());
    }

    @Override
    @Transactional
    public StudentDetailResponseDTO updateStudentByAdmin(UUID userId, StudentUpdateDTO dto) {
        StudentEntity student = studentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên!"));
        
        // Cập nhật thông tin học vụ
        student.setFullName(dto.getFullName());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setHometown(dto.getHometown());
        student.setAddress(dto.getAddress());
        student.setClassName(dto.getClassName());
        student.setCumulativeGpa(dto.getCumulativeGpa());
        student.setAcademicStatus(dto.getAcademicStatus());

        // Cập nhật thông tin User liên đới
        UserEntity user = student.getUser();
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setIsActive(dto.getIsActive());

        return mapToStudentDetailResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void softDeleteStudent(UUID userId, StudentDeleteRequestDTO dto) {
        StudentEntity student = studentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên!"));
        
        UserEntity user = student.getUser();
        
        LocalDateTime now = LocalDateTime.now();
        student.setDeletedAt(now);
        user.setDeletedAt(now);
        user.setIsActive(false); // Khóa tài khoản
        
        userRepository.save(user);
    }

    // =========================================================================
    // 3. LUỒNG NGHIỆP VỤ SINH VIÊN (STUDENT)
    // =========================================================================
    @Override
    public List<StudentDetailResponseDTO> getStudentsByClassName(String className) {
        return studentRepository.findAllByClassNameAndDeletedAtIsNull(className)
                .stream()
                .map(student -> mapToStudentDetailResponse(student.getUser()))
                .toList();
    }

    @Override
    @Transactional
    public StudentDetailResponseDTO updateOwnProfile(UUID userId, StudentUpdateDTO dto) {
        StudentEntity student = studentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ!"));
        
        student.setAddress(dto.getAddress());
        student.setHometown(dto.getHometown());
        
        UserEntity user = student.getUser();
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAvatarUrl(dto.getAvatarUrl());

        return mapToStudentDetailResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequestDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // 1. So sánh mật khẩu cũ
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không trùng khớp!");
        }

        // 3. Chống trùng mật khẩu cũ
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        // 4. Băm mật khẩu và lưu
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // =========================================================================
    // 4. HÀM PHỤ TRỢ (HELPER METHOD)
    // =========================================================================
    private StudentDetailResponseDTO mapToStudentDetailResponse(UserEntity user) {
        StudentEntity student = user.getStudent();
        
        return StudentDetailResponseDTO.builder()
                // Dữ liệu User
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                
                // Dữ liệu Student
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .nationalId(student.getNationalId())
                .hometown(student.getHometown())
                .address(student.getAddress())
                .faculty(student.getFaculty())
                .major(student.getMajor())
                .className(student.getClassName())
                .enrollmentYear(student.getEnrollmentYear())
                .expectedGraduationYear(student.getExpectedGraduationYear())
                .cumulativeGpa(student.getCumulativeGpa())
                .academicStatus(student.getAcademicStatus())
                
                // Vết lưu trữ
                .userCreatedAt(user.getCreatedAt())
                .studentCreatedAt(student.getCreatedAt())
                .build();
    }
}