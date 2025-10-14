package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCode

class JwtException(message: String = ErrorCode.JWT_UNAUTHORIZED_EXCEPTION.message) :
    CustomException(ErrorCode.JWT_UNAUTHORIZED_EXCEPTION, message)
