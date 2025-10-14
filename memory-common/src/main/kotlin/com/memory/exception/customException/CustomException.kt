package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCode

open class CustomException(
    val errorCode: ErrorCode,
    message: String? = errorCode.message
) : RuntimeException(message)
