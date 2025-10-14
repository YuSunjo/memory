package com.memory.domain.notification

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Firebase Cloud Messaging 토큰을 관리하는 Entity
 * 사용자의 디바이스별 FCM 토큰을 저장하고 관리
 */
@Entity
@Table(
    name = "fcm_token",
    indexes = [
        Index(name = "idx_fcm_token_member", columnList = "member_id"),
        Index(name = "idx_fcm_token_device", columnList = "device_id"),
        Index(name = "idx_fcm_token_active", columnList = "is_active")
    ]
)
class FcmToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @Column(name = "token_value", nullable = false, length = 1000)
    var tokenValue: String,

    @Column(name = "device_id", nullable = false, length = 255)
    var deviceId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    var deviceType: DeviceType,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "last_used_at")
    var lastUsedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime = LocalDateTime.now().plusDays(60) // FCM 토큰은 일반적으로 60일 후 만료
) : BaseTimeEntity() {

    fun updateToken(newTokenValue: String) {
        tokenValue = newTokenValue
        lastUsedAt = LocalDateTime.now()
        expiresAt = LocalDateTime.now().plusDays(60)
        isActive = true
    }

    fun updateLastUsed() {
        lastUsedAt = LocalDateTime.now()
    }

    fun deactivate() {
        isActive = false
    }

    fun activate() {
        isActive = true
        lastUsedAt = LocalDateTime.now()
    }

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    fun isUsable(): Boolean = isActive && !isExpired() && !isDeleted()
}
