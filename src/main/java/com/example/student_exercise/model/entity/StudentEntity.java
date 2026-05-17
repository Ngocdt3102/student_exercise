package com.example.student_exercise.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId; 

    @Column(name = "student_code", nullable = false, unique = true, length = 20)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth; 

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "national_id", nullable = false, unique = true, length = 12)
    private String nationalId;

    @Column(name = "hometown", length = 100)
    private String hometown;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "faculty", length = 100)
    private String faculty;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Column(name = "expected_graduation_year")
    private Integer expectedGraduationYear;

    @Column(name = "actual_graduation_year")
    private Integer actualGraduationYear;

    @Column(name = "cumulative_gpa", precision = 3, scale = 2)
    private BigDecimal cumulativeGpa; 

    @Column(name = "academic_status", length = 20)
    private String academicStatus;

    // --- [BỔ SUNG] Các trường Auditing riêng cho cấu trúc hồ sơ sinh viên ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    // --- [BỔ SUNG] Các trường Soft Delete riêng cho cấu trúc hồ sơ sinh viên ---
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @OneToOne
    @MapsId 
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.cumulativeGpa == null) {
            this.cumulativeGpa = BigDecimal.ZERO; 
        }
        if (this.academicStatus == null) {
            this.academicStatus = "studying"; 
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}