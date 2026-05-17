CREATE DATABASE STUDENT_EXERCISE;
USE STUDENT_EXERCISE;

-- =======================================================
-- B?NG 1: USERS
-- =======================================================
CREATE TABLE users (
    -- Dùng UNIQUEIDENTIFIER cho UUID và t? ??ng sinh mã b?ng NEWID()
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone_number NVARCHAR(15) UNIQUE,
    avatar_url NVARCHAR(255),
    
    role NVARCHAR(20) DEFAULT 'student',
    is_active BIT DEFAULT 1, -- 1 t??ng ???ng v?i TRUE
    last_login DATETIME2 NULL,
    
    -- Dùng DATETIME2 và GETDATE() thay cho TIMESTAMP
    created_at DATETIME2 DEFAULT GETDATE(),
    created_by UNIQUEIDENTIFIER NULL,
    updated_at DATETIME2 DEFAULT GETDATE(),
    updated_by UNIQUEIDENTIFIER NULL,
    
    deleted_at DATETIME2 NULL,
    deleted_by UNIQUEIDENTIFIER NULL
);

-- =======================================================
-- B?NG 2: STUDENTS
-- =======================================================
CREATE TABLE students (
    user_id UNIQUEIDENTIFIER PRIMARY KEY,
    student_code NVARCHAR(20) NOT NULL UNIQUE,
    
    -- Dùng NVARCHAR ?? l?u ti?ng Vi?t (VD: ??ng Th? Ng?c)
    full_name NVARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender NVARCHAR(10) NOT NULL,
    national_id NVARCHAR(12) NOT NULL UNIQUE,
    hometown NVARCHAR(100),
    address NVARCHAR(255),
    
    faculty NVARCHAR(100),
    major NVARCHAR(100),
    class_name NVARCHAR(50),
    
    enrollment_year INT,
    expected_graduation_year INT,
    actual_graduation_year INT,
    
    cumulative_gpa DECIMAL(3,2) DEFAULT 0.00,
    academic_status NVARCHAR(20) DEFAULT 'studying',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);