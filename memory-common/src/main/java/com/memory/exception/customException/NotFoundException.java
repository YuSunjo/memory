package com.memory.exception.customException;

import com.memory.exception.errorCode.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
