package com.memory.domain.notification

import com.memory.domain.BaseTimeEntity
import jakarta.persistence.*

/**
 * 알림 템플릿을 관리하는 Entity
 * 재사용 가능한 알림 메시지 템플릿을 저장하고 관리
 */
@Entity
@Table(
    name = "notification_template",
    indexes = [
        Index(name = "idx_template_type", columnList = "notification_type"),
        Index(name = "idx_template_active", columnList = "is_active")
    ]
)
class NotificationTemplate private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    var notificationType: NotificationType,

    @Column(name = "template_name", nullable = false, length = 100)
    var templateName: String,

    /**
     * 알림 제목 템플릿 (변수 포함 가능)
     * 예: "{senderName}님이 메모리를 공유했습니다"
     */
    @Column(name = "title_template", nullable = false, length = 255)
    var titleTemplate: String,

    /**
     * 알림 메시지 템플릿 (변수 포함 가능)
     * 예: "{memoryTitle} 메모리가 공유되었습니다. 확인해보세요!"
     */
    @Column(name = "message_template", nullable = false, length = 1000)
    var messageTemplate: String,

    /**
     * Firebase FCM에서 사용할 추가 데이터 템플릿 (JSON 형태)
     */
    @Column(name = "payload_template", columnDefinition = "TEXT")
    var payloadTemplate: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "description", length = 500)
    var description: String? = null,

    @Column(name = "version", nullable = false)
    var version: Int = 1
) : BaseTimeEntity() {

    companion object {
        fun create(
            notificationType: NotificationType,
            templateName: String,
            titleTemplate: String,
            messageTemplate: String
        ): NotificationTemplate {
            return NotificationTemplate(
                notificationType = notificationType,
                templateName = templateName,
                titleTemplate = titleTemplate,
                messageTemplate = messageTemplate
            )
        }

        fun create(
            notificationType: NotificationType,
            templateName: String,
            titleTemplate: String,
            messageTemplate: String,
            payloadTemplate: String
        ): NotificationTemplate {
            return NotificationTemplate(
                notificationType = notificationType,
                templateName = templateName,
                titleTemplate = titleTemplate,
                messageTemplate = messageTemplate,
                payloadTemplate = payloadTemplate
            )
        }
    }

    fun updateTemplate(titleTemplate: String, messageTemplate: String, payloadTemplate: String?) {
        this.titleTemplate = titleTemplate
        this.messageTemplate = messageTemplate
        this.payloadTemplate = payloadTemplate
        this.version++
    }

    fun updateActiveStatus(isActive: Boolean) {
        this.isActive = isActive
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun isUsable(): Boolean = isActive && !isDeleted()

    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 제목 생성
     * 예: "{senderName}님이 메모리를 공유했습니다" -> "홍길동님이 메모리를 공유했습니다"
     */
    fun generateTitle(variables: Map<String, Any?>?): String {
        var result = titleTemplate
        variables?.forEach { (key, value) ->
            val placeholder = "{$key}"
            val replacementValue = value?.toString() ?: ""
            result = result.replace(placeholder, replacementValue)
        }
        return result
    }

    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 메시지 생성
     */
    fun generateMessage(variables: Map<String, Any?>?): String {
        var result = messageTemplate
        variables?.forEach { (key, value) ->
            val placeholder = "{$key}"
            val replacementValue = value?.toString() ?: ""
            result = result.replace(placeholder, replacementValue)
        }
        return result
    }

    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 페이로드 생성
     */
    fun generatePayload(variables: Map<String, Any?>?): String? {
        if (payloadTemplate == null) return null

        var result = payloadTemplate!!
        variables?.forEach { (key, value) ->
            val placeholder = "{$key}"
            val replacementValue = value?.toString() ?: ""
            result = result.replace(placeholder, replacementValue)
        }
        return result
    }
}
