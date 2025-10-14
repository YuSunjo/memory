package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCode

class NotFoundException(message: String) : CustomException(ErrorCode.NOT_FOUND, message)
