package com.memory.exception.customException;

import com.memory.exception.errorCode.ErrorCode;

public class ValidationException extends CustomException {

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_EXCEPTION, message);
    }
}
