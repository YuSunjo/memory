package com.memory.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND(404, "존재하지 않습니다."),
    VALIDATION_EXCEPTION(400, "잘못된 입력입니다."),
    CONFLICT_EXCEPTION(409, "이미 존재하는 데이터입니다."),
    JWT_UNAUTHORIZED_EXCEPTION(401, "인증되지 않은 사용자입니다."),
    ;

    private final int statusCode;
    private final String message;
}
