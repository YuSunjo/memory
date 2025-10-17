package com.memory.config.exception

import com.memory.exception.customException.ConflictException
import com.memory.exception.customException.JwtException
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import com.memory.response.ServerResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionController {
    private val log = LoggerFactory.getLogger(ExceptionController::class.java)

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(e: NotFoundException): ServerResponse<Any?> {
        log.error("NotFoundException occurred: {}, status code: {}", e.message, e.errorCode.statusCode, e)
        return ServerResponse.error(e.errorCode.statusCode, e.message)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleException(e: ConflictException): ServerResponse<Any?> {
        log.error("ConflictException occurred: {}, status code: {}", e.message, e.errorCode.statusCode, e)
        return ServerResponse.error(e.errorCode.statusCode, e.message)
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: ValidationException): ServerResponse<Any?> {
        log.error("ValidationException occurred: {}, status code: {}", e.message, e.errorCode.statusCode, e)
        return ServerResponse.error(e.errorCode.statusCode, e.message)
    }

    @ExceptionHandler(JwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: JwtException): ServerResponse<Any?> {
        log.error("JwtException occurred: {}, status code: {}", e.message, e.errorCode.statusCode, e)
        return ServerResponse.error(e.errorCode.statusCode, e.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ServerResponse<Any?> {
        log.error("Unexpected exception occurred: {}", e.message, e)
        return ServerResponse.error(500, "Internal server error: ${e.message}")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: MethodArgumentNotValidException): ServerResponse<Any?> {
        val errorMessage = e.bindingResult.fieldError?.defaultMessage ?: "Validation error occurred"
        log.error("MethodArgumentNotValidException occurred: {}", errorMessage, e)
        return ServerResponse.error(400, errorMessage)
    }
}
