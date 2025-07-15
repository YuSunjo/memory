package com.memory.domain.notification.repository;

import com.memory.domain.notification.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, NotificationTemplateRepositoryCustom {
}
