package com.example.student_exercise.repository;

import com.example.student_exercise.model.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {

    // =========================================================================
    // 1. TRUY VẤN TÌM KIẾM HỒ SƠ SINH VIÊN
    // =========================================================================
    
    // Tìm sinh viên bằng ID (UUID) và hồ sơ chưa bị xóa mềm
    Optional<StudentEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    // Tìm sinh viên bằng Mã số sinh viên (MSSV) và hồ sơ chưa bị xóa mềm
    Optional<StudentEntity> findByStudentCodeAndDeletedAtIsNull(String studentCode);


    // =========================================================================
    // 2. TRUY VẤN KIỂM TRA ĐỊNH DANH DUY NHẤT (Chống trùng lặp học vụ)
    // =========================================================================
    
    // Kiểm tra MSSV đã tồn tại chưa
    boolean existsByStudentCode(String studentCode);

    // Kiểm tra số CCCD đã tồn tại chưa
    boolean existsByNationalId(String nationalId);


    // =========================================================================
    // 3. TRUY VẤN NÂNG CAO / TÌM KIẾM THEO BỘ LỌC (FILTER)
    // =========================================================================
    
    // Thay thế hàm cũ bằng hàm có chứa Pageable để phân trang dưới DB
    Page<StudentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    // Tìm kiếm danh sách sinh viên theo Lớp (Ví dụ: Tìm tất cả sinh viên lớp ST23C)
    List<StudentEntity> findAllByClassNameAndDeletedAtIsNull(String className);

    // Sử dụng JPQL Custom Query để tìm kiếm sinh viên theo từ khóa (Họ tên hoặc MSSV)
    // Thao tác JOIN ngầm giữa bảng Student và User để đảm bảo cả 2 đều chưa bị xóa mềm
    @Query("SELECT s FROM StudentEntity s WHERE s.deletedAt IS NULL AND s.user.deletedAt IS NULL " +
           "AND (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<StudentEntity> searchActiveStudents(@Param("keyword") String keyword);
}