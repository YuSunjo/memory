package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCodeKT

class ValidationExceptionKT(message: String) : CustomExceptionKT(ErrorCodeKT.VALIDATION_EXCEPTION, message)
