package com.wcs.spring_data_jpa_project.ExceptionHandeller;


import com.wcs.spring_data_jpa_project.exception.DepartmentNotFoundException;
import com.wcs.spring_data_jpa_project.exception.InvalidInputException;
import com.wcs.spring_data_jpa_project.exception.UserAlreadyExistsException;
import com.wcs.spring_data_jpa_project.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<String> handleDepartmentNotFound(DepartmentNotFoundException ex) {
        log.error("Department not found: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        log.error("Invalid input: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Internal server error");
    }
}

