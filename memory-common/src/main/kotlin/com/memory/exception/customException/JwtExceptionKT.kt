package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCodeKT

class JwtExceptionKT(message: String = ErrorCodeKT.JWT_UNAUTHORIZED_EXCEPTION.message) :
    CustomExceptionKT(ErrorCodeKT.JWT_UNAUTHORIZED_EXCEPTION, message)
