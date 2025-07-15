package com.memory.domain.notification.repository;

import com.memory.domain.notification.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long>, FcmTokenRepositoryCustom {
}
