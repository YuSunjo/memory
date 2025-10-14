package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCodeKT

class ConflictExceptionKT(message: String = ErrorCodeKT.CONFLICT_EXCEPTION.message) :
    CustomExceptionKT(ErrorCodeKT.CONFLICT_EXCEPTION, message)
