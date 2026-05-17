package com.example.student_exercise.service;

import com.example.student_exercise.model.dto.auth.*;
import com.example.student_exercise.model.dto.user.UserDetailResponseDTO;

import java.util.UUID;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO dto);
    UserDetailResponseDTO register(RegisterRequestDTO dto);
    void forgotPassword(ForgotPasswordRequestDTO dto);
    void resetPassword(ResetPasswordRequestDTO dto);
    void changePassword(UUID userId, ChangePasswordRequestDTO dto);
}