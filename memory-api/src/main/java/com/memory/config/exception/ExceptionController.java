package com.memory.config.exception;

import com.memory.config.OneTimePoolReset;
import com.memory.exception.customException.JwtException;
import com.memory.response.ServerResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final OneTimePoolReset oneTimePoolReset;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public ServerResponse<Object> handleException(NotFoundException e) {
        log.error("NotFoundException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ServerResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
    public ServerResponse<Object> handleException(ConflictException e) {
        log.error("ConflictException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ServerResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public ServerResponse<Object> handleException(ValidationException e) {
        log.error("ValidationException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ServerResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    public ServerResponse<Object> handleException(JwtException e) {
        log.error("JwtException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        
        // JWT 예외 발생시 처리
        try {
            // 첫 번째 JWT 예외에서만 전체 풀 리셋 (이후에는 스킵)
            oneTimePoolReset.executeOnFirstJwtException();
        } catch (Exception cleanupException) {
            log.warn("JWT exception cleanup failed", cleanupException);
        }
        
        return ServerResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerResponse<Object> handleException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        return ServerResponse.error(500, "Internal server error: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public ServerResponse<Object> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : "Validation error occurred";
        log.error("MethodArgumentNotValidException occurred: {}", errorMessage, e);
        return ServerResponse.error(400, errorMessage);
    }

}
