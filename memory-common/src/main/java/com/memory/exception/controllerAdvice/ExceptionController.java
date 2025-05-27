package com.memory.exception.controllerAdvice;

import com.memory.exception.customException.JwtException;
import com.memory.response.ApiResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Object> handleException(NotFoundException e) {
        log.error("NotFoundException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ApiResponse<Object> handleException(ConflictException e) {
        log.error("ConflictException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<Object> handleException(ValidationException e) {
        log.error("ValidationException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ApiResponse<Object> handleException(JwtException e) {
        log.error("JwtException occurred: {}, status code: {}", e.getMessage(), e.getErrorCode().getStatusCode(), e);
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

}
