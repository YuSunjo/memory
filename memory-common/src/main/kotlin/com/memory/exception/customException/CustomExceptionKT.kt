package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCodeKT

open class CustomExceptionKT(
    val errorCode: ErrorCodeKT,
    message: String? = errorCode.message
) : RuntimeException(message)
