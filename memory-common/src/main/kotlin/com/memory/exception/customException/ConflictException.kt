package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCode

class ConflictException(message: String = ErrorCode.CONFLICT_EXCEPTION.message) :
    CustomException(ErrorCode.CONFLICT_EXCEPTION, message)
