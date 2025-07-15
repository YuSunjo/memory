package com.memory.domain.notification;

import com.memory.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 템플릿을 관리하는 Entity
 * 재사용 가능한 알림 메시지 템플릿을 저장하고 관리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "notification_template",
    indexes = {
        @Index(name = "idx_template_type", columnList = "notification_type"),
        @Index(name = "idx_template_active", columnList = "is_active")
    }
)
public class NotificationTemplate extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;
    
    /**
     * 알림 제목 템플릿 (변수 포함 가능)
     * 예: "{senderName}님이 메모리를 공유했습니다"
     */
    @Column(name = "title_template", nullable = false, length = 255)
    private String titleTemplate;
    
    /**
     * 알림 메시지 템플릿 (변수 포함 가능)
     * 예: "{memoryTitle} 메모리가 공유되었습니다. 확인해보세요!"
     */
    @Column(name = "message_template", nullable = false, length = 1000)
    private String messageTemplate;
    
    /**
     * Firebase FCM에서 사용할 추가 데이터 템플릿 (JSON 형태)
     */
    @Column(name = "payload_template", columnDefinition = "TEXT")
    private String payloadTemplate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    public NotificationTemplate(NotificationType notificationType, String templateName,
                               String titleTemplate, String messageTemplate) {
        this.notificationType = notificationType;
        this.templateName = templateName;
        this.titleTemplate = titleTemplate;
        this.messageTemplate = messageTemplate;
        this.isActive = true;
        this.version = 1;
    }
    
    public NotificationTemplate(NotificationType notificationType, String templateName,
                               String titleTemplate, String messageTemplate, 
                               String payloadTemplate) {
        this(notificationType, templateName, titleTemplate, messageTemplate);
        this.payloadTemplate = payloadTemplate;
    }
    
    public void updateTemplate(String titleTemplate, String messageTemplate, String payloadTemplate) {
        this.titleTemplate = titleTemplate;
        this.messageTemplate = messageTemplate;
        this.payloadTemplate = payloadTemplate;
        this.version++;
    }
    
    public void updateActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public boolean isUsable() {
        return isActive && !isDeleted();
    }
    
    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 제목 생성
     * 예: "{senderName}님이 메모리를 공유했습니다" -> "홍길동님이 메모리를 공유했습니다"
     */
    public String generateTitle(java.util.Map<String, Object> variables) {
        String result = titleTemplate;
        if (variables != null) {
            for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        return result;
    }
    
    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 메시지 생성
     */
    public String generateMessage(java.util.Map<String, Object> variables) {
        String result = messageTemplate;
        if (variables != null) {
            for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        return result;
    }
    
    /**
     * 변수가 포함된 템플릿에서 실제 값으로 치환하여 페이로드 생성
     */
    public String generatePayload(java.util.Map<String, Object> variables) {
        if (payloadTemplate == null) {
            return null;
        }
        
        String result = payloadTemplate;
        if (variables != null) {
            for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        return result;
    }
}
