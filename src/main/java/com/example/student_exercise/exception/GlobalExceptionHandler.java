package com.example.student_exercise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Đánh dấu đây là file bắt lỗi toàn cục cho các @RestController
public class GlobalExceptionHandler {

    /**
     * 1. BẮT LỖI NGHIỆP VỤ (Business Logic Error)
     * Bắt các lỗi throw new RuntimeException(...) mà chúng ta tự viết trong file Service
     * Ví dụ: "Mật khẩu hiện tại không chính xác", "Email đã tồn tại"
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // Trả về mã 400 Bad Request
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage()) // Lấy đúng câu chữ mình đã throw trong Service
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2. BẮT LỖI VALIDATE DỮ LIỆU ĐẦU VÀO (@Valid)
     * Khi người dùng nhập thiếu dữ liệu hoặc sai định dạng email, CCCD...
     * Spring Boot sẽ ném ra lỗi MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Gom tất cả các lỗi của các trường dữ liệu lại thành một danh sách (Map)
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(errors) // Trả về danh sách chi tiết trường nào bị lỗi gì
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. BẮT CÁC LỖI HỆ THỐNG CÒN LẠI (Fallback Exception)
     * Đề phòng trường hợp code bị lỗi NullPointer hoặc đứt kết nối Database
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // Lỗi 500
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Lỗi hệ thống nội bộ. Vui lòng liên hệ quản trị viên!")
                .build();

        // In log ra console để Developer biết đường sửa ngầm
        ex.printStackTrace();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}