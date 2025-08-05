package com.memory.dto.notification;

import com.memory.domain.notification.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FcmTokenRequest(
        
        @NotNull(message = "사용자 ID는 필수입니다")
        Long memberId,
        
        @NotBlank(message = "FCM 토큰은 필수입니다")
        @Size(max = 1000, message = "FCM 토큰은 1000자 이하여야 합니다")
        String tokenValue,
        
        @NotBlank(message = "디바이스 ID는 필수입니다")
        @Size(max = 255, message = "디바이스 ID는 255자 이하여야 합니다")
        String deviceId,
        
        @NotNull(message = "디바이스 타입은 필수입니다")
        DeviceType deviceType
) {
    
}
