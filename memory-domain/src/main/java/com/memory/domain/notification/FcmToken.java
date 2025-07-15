package com.memory.domain.notification;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Firebase Cloud Messaging 토큰을 관리하는 Entity
 * 사용자의 디바이스별 FCM 토큰을 저장하고 관리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "fcm_token",
    indexes = {
        @Index(name = "idx_fcm_token_member", columnList = "member_id"),
        @Index(name = "idx_fcm_token_device", columnList = "device_id"),
        @Index(name = "idx_fcm_token_active", columnList = "is_active")
    }
)
public class FcmToken extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "token_value", nullable = false, length = 1000)
    private String tokenValue;
    
    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    public FcmToken(Member member, String tokenValue, String deviceId, DeviceType deviceType) {
        this.member = member;
        this.tokenValue = tokenValue;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.isActive = true;
        this.lastUsedAt = LocalDateTime.now();
        // FCM 토큰은 일반적으로 60일 후 만료
        this.expiresAt = LocalDateTime.now().plusDays(60);
    }
    
    public void updateToken(String newTokenValue) {
        this.tokenValue = newTokenValue;
        this.lastUsedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(60);
        this.isActive = true;
    }
    
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public void activate() {
        this.isActive = true;
        this.lastUsedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isUsable() {
        return isActive && !isExpired() && !isDeleted();
    }
}
