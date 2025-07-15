package com.memory.dto.notification;

import com.memory.domain.notification.DeviceType;
import com.memory.domain.notification.FcmToken;

import java.time.LocalDateTime;

public record FcmTokenResponse(
        Long id,
        Long memberId,
        String tokenValue,
        String deviceId,
        DeviceType deviceType,
        Boolean isActive,
        LocalDateTime lastUsedAt,
        LocalDateTime expiresAt,
        LocalDateTime createDate
) {
    
    /**
     * Entity -> DTO 변환
     */
    public static FcmTokenResponse from(FcmToken fcmToken) {
        return new FcmTokenResponse(
                fcmToken.getId(),
                fcmToken.getMember().getId(),
                fcmToken.getTokenValue(),
                fcmToken.getDeviceId(),
                fcmToken.getDeviceType(),
                fcmToken.getIsActive(),
                fcmToken.getLastUsedAt(),
                fcmToken.getExpiresAt(),
                fcmToken.getCreateDate()
        );
    }
    
    /**
     * 보안상 토큰 값을 마스킹한 응답 DTO
     */
    public record Masked(
            Long id,
            String maskedToken,
            String deviceId,
            DeviceType deviceType,
            Boolean isActive,
            LocalDateTime lastUsedAt,
            LocalDateTime expiresAt,
            LocalDateTime createDate
    ) {
        public static Masked from(FcmToken fcmToken) {
            String masked = maskToken(fcmToken.getTokenValue());
            return new Masked(
                    fcmToken.getId(),
                    masked,
                    fcmToken.getDeviceId(),
                    fcmToken.getDeviceType(),
                    fcmToken.getIsActive(),
                    fcmToken.getLastUsedAt(),
                    fcmToken.getExpiresAt(),
                    fcmToken.getCreateDate()
            );
        }
        
        private static String maskToken(String token) {
            if (token == null || token.length() < 10) {
                return "***";
            }
            return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
        }
    }
    
    /**
     * 간단한 토큰 정보만 포함하는 요약 DTO
     */
    public record Summary(
            Long id,
            String deviceId,
            DeviceType deviceType,
            Boolean isActive,
            LocalDateTime lastUsedAt
    ) {
        public static Summary from(FcmToken fcmToken) {
            return new Summary(
                    fcmToken.getId(),
                    fcmToken.getDeviceId(),
                    fcmToken.getDeviceType(),
                    fcmToken.getIsActive(),
                    fcmToken.getLastUsedAt()
            );
        }
    }
}
