package com.memory.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 디바이스 타입을 정의하는 enum
 * FCM 토큰 관리에서 사용되는 디바이스 유형들을 정의
 */
@Getter
@RequiredArgsConstructor
public enum DeviceType {
    
    ANDROID("Android"),
    IOS("iOS"),
    WEB("Web");
    
    private final String displayName;
}
