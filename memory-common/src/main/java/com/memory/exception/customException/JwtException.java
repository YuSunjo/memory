package com.memory.exception.customException;

import com.memory.exception.errorCode.ErrorCode;

public class JwtException extends CustomException {

    public JwtException(String message) {
        super(ErrorCode.JWT_UNAUTHORIZED_EXCEPTION, message);
    }

    public JwtException() {
        super(ErrorCode.JWT_UNAUTHORIZED_EXCEPTION, ErrorCode.JWT_UNAUTHORIZED_EXCEPTION.getMessage());
    }
}
