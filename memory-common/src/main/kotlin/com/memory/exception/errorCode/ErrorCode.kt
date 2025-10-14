package com.memory.exception.errorCode

enum class ErrorCode(
    val statusCode: Int,
    val message: String
) {
    NOT_FOUND(404, "존재하지 않습니다."),
    VALIDATION_EXCEPTION(400, "잘못된 입력입니다."),
    CONFLICT_EXCEPTION(409, "이미 존재하는 데이터입니다."),
    JWT_UNAUTHORIZED_EXCEPTION(401, "인증되지 않은 사용자입니다.")
}
