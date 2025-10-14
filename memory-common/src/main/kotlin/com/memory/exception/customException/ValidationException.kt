package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCode

class ValidationException(message: String) : CustomException(ErrorCode.VALIDATION_EXCEPTION, message)
