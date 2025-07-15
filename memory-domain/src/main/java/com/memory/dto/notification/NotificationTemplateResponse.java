package com.memory.dto.notification;

import com.memory.domain.notification.NotificationTemplate;
import com.memory.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationTemplateResponse(
        Long id,
        NotificationType notificationType,
        String templateName,
        String titleTemplate,
        String messageTemplate,
        String payloadTemplate,
        Boolean isActive,
        String description,
        Integer version,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {
    
    public static NotificationTemplateResponse from(NotificationTemplate template) {
        return new NotificationTemplateResponse(
                template.getId(),
                template.getNotificationType(),
                template.getTemplateName(),
                template.getTitleTemplate(),
                template.getMessageTemplate(),
                template.getPayloadTemplate(),
                template.getIsActive(),
                template.getDescription(),
                template.getVersion(),
                template.getCreateDate(),
                template.getUpdateDate()
        );
    }
    
    public record Summary(
            Long id,
            NotificationType notificationType,
            String templateName,
            Boolean isActive,
            Integer version,
            LocalDateTime updateDate
    ) {
        public static Summary from(NotificationTemplate template) {
            return new Summary(
                    template.getId(),
                    template.getNotificationType(),
                    template.getTemplateName(),
                    template.getIsActive(),
                    template.getVersion(),
                    template.getUpdateDate()
            );
        }
    }
    
    public record ListItem(
            Long id,
            NotificationType notificationType,
            String templateName,
            String description,
            Boolean isActive,
            Integer version
    ) {
        public static ListItem from(NotificationTemplate template) {
            return new ListItem(
                    template.getId(),
                    template.getNotificationType(),
                    template.getTemplateName(),
                    template.getDescription(),
                    template.getIsActive(),
                    template.getVersion()
            );
        }
    }
}
