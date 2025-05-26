package com.memory.exception.controllerAdvice;

import com.memory.response.ApiResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Object> handleException(NotFoundException e) {
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ApiResponse<Object> handleException(ConflictException e) {
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<Object> handleException(ValidationException e) {
        return ApiResponse.error(e.getErrorCode().getStatusCode(), e.getMessage());
    }

}
