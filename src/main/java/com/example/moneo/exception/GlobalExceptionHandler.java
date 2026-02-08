package com.example.moneo.exception;

import com.example.moneo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex) {
        String message = ex.getMessage();


        switch (message) {
            case "EMAIL_EXISTS":
                return buildResponse(HttpStatus.CONFLICT, "EMAIL_EXISTS", "Bu email artıq qeydiyyatdan keçib.");

            case "WRONG_CREDENTIALS":
                return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Email və ya şifrə yanlışdır.");

            case "TOO_MANY_REQUESTS":
                return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "Çox sayda sorğu göndərildi. Bir az gözləyin.");

            case "CATEGORY_HAS_TRANSACTIONS":
                return buildResponse(HttpStatus.BAD_REQUEST, "CATEGORY_HAS_TRANSACTIONS", "Bu kateqoriyaya bağlı əməliyyatlar olduğu üçün silinə bilməz.");

            default:

                return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", message);
        }
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .error(error)
                .message(message)
                .build();
        return new ResponseEntity<>(response, status);
    }
}