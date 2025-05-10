package com.wcs.spring_data_jpa_project.ExceptionHandeller;

import com.wcs.spring_data_jpa_project.customeResponse.ApiResponse;
import com.wcs.spring_data_jpa_project.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("User not found", ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("User already exists", ex.getMessage()));
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleDepartmentNotFound(DepartmentNotFoundException ex) {
        log.error("Department not found: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("Department not found", ex.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidInput(InvalidInputException ex) {
        log.error("Invalid input: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid input", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid credentials", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateResource(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("Duplicate resource", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(
                new ApiResponse<>("Internal server error", ex.getMessage())
        );
    }
}
