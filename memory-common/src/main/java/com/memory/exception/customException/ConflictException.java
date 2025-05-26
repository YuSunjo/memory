package com.memory.exception.customException;

import com.memory.exception.errorCode.ErrorCode;

public class ConflictException extends CustomException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT_EXCEPTION, message);
    }

    public ConflictException() {
        super(ErrorCode.CONFLICT_EXCEPTION, ErrorCode.CONFLICT_EXCEPTION.getMessage());
    }
}
