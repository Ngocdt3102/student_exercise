package com.example.student_exercise.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // =========================================================================
    // CẤU HÌNH CORS (MỞ CỔNG CHO FRONTEND)
    // =========================================================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Đã bổ sung thêm cổng 5500 của VS Code Live Server
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "http://localhost:5173", 
            "http://localhost:4200",
            "http://127.0.0.1:5500", // Cổng IP thực của Live Server
            "http://localhost:5500"  // Cổng Localhost dự phòng của Live Server
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Kích hoạt cấu hình CORS vừa định nghĩa ở trên
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .csrf(AbstractHttpConfigurer::disable)
            // Tắt Session, sử dụng JWT Stateless 100%
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Mở cửa cho Swagger UI sinh tài liệu
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 2. Mở cửa cho các luồng xác thực công cộng (Đăng nhập, Đăng ký, Quên mật khẩu)
                .requestMatchers("/api/auth/**").permitAll() 
                
                // 3. KHÓA CHẶT: Chỉ tài khoản có ROLE 'admin' mới được quản lý hệ thống User
                .requestMatchers("/api/users/**").hasRole("admin")
                
                // 4. TẤT CẢ các API còn lại (bao gồm cả /api/students/**) ĐỀU PHẢI CÓ TOKEN mới được vào
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        // Thêm màng lọc JWT đứng trước màng lọc xác thực chuẩn của Spring
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}