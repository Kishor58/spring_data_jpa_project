package com.wcs.spring_data_jpa_project.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
