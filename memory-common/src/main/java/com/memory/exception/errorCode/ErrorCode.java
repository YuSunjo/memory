package com.memory.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND(404, "존재하지 않습니다."),
    VALIDATION_EXCEPTION(400, "이미 존재합니다."),
    CONFLICT_EXCEPTION(409, "잘못된 입력입니다.")
    ;

    private final int statusCode;
    private final String message;
}
