package com.memory.exception.customException

import com.memory.exception.errorCode.ErrorCodeKT

class NotFoundExceptionKT(message: String) : CustomExceptionKT(ErrorCodeKT.NOT_FOUND, message)
