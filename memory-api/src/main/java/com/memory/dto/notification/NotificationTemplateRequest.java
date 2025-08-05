package com.memory.dto.notification;

import com.memory.domain.notification.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationTemplateRequest(
        
        @NotNull(message = "알림 타입은 필수입니다")
        NotificationType notificationType,
        
        @NotBlank(message = "템플릿 이름은 필수입니다")
        @Size(max = 100, message = "템플릿 이름은 100자 이하여야 합니다")
        String templateName,
        
        @NotBlank(message = "제목 템플릿은 필수입니다")
        @Size(max = 255, message = "제목 템플릿은 255자 이하여야 합니다")
        String titleTemplate,
        
        @NotBlank(message = "메시지 템플릿은 필수입니다")
        @Size(max = 1000, message = "메시지 템플릿은 1000자 이하여야 합니다")
        String messageTemplate,
        
        String payloadTemplate,
        
        @Size(max = 500, message = "설명은 500자 이하여야 합니다")
        String description,
        
        Boolean isActive
) {
    
    public NotificationTemplateRequest {
        // 기본값 설정
        if (isActive == null) {
            isActive = true;
        }
    }
    
    public static NotificationTemplateRequest create(
            NotificationType notificationType,
            String templateName,
            String titleTemplate,
            String messageTemplate) {
        return new NotificationTemplateRequest(
                notificationType, templateName, titleTemplate, messageTemplate,
                null, null, true
        );
    }
    
    public static NotificationTemplateRequest createWithPayload(
            NotificationType notificationType,
            String templateName,
            String titleTemplate,
            String messageTemplate,
            String payloadTemplate) {
        return new NotificationTemplateRequest(
                notificationType, templateName, titleTemplate, messageTemplate,
                payloadTemplate, null, true
        );
    }
}
