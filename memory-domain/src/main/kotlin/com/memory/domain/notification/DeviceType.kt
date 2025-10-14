package com.memory.domain.notification

/**
 * 디바이스 타입을 정의하는 enum
 * FCM 토큰 관리에서 사용되는 디바이스 유형들을 정의
 */
enum class DeviceType(val displayName: String) {
    ANDROID("Android"),
    IOS("iOS"),
    WEB("Web")
}
