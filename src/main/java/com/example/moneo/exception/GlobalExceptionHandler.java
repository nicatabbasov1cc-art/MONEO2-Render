package com.example.moneo.exception;

import com.example.moneo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex) {
        String message = ex.getMessage();

        switch (message) {
            case "PASSWORDS_DO_NOT_MATCH":
                return buildResponse(HttpStatus.BAD_REQUEST, "PASSWORDS_DO_NOT_MATCH", "Şifrələr bir-biri ilə eyni deyil.");
            case "ACCESS_DENIED":
                return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Sizin bu əməliyyatı yerinə yetirmək üçün icazəniz yoxdur.");
            case "TRANSACTION_NOT_FOUND":
                return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", "Tranzaksiya tapılmadı.");
            case "EMAIL_EXISTS":
                return buildResponse(HttpStatus.CONFLICT, "EMAIL_EXISTS", "Bu email artıq qeydiyyatdan keçib.");
            case "WRONG_CREDENTIALS":
                return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Email və ya şifrə yanlışdır.");
            case "TOO_MANY_REQUESTS":
                return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "Çox sayda sorğu göndərildi. Bir az gözləyin.");
            case "CATEGORY_HAS_TRANSACTIONS":
                return buildResponse(HttpStatus.BAD_REQUEST, "CATEGORY_HAS_TRANSACTIONS", "Bu kateqoriyaya bağlı əməliyyatlar olduğu üçün silinə bilməz.");
            case "ACCOUNT_NOT_FOUND":
                return buildResponse(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "Hesab tapılmadı.");
            case "CATEGORY_NOT_FOUND":
                return buildResponse(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "Kateqoriya tapılmadı.");
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