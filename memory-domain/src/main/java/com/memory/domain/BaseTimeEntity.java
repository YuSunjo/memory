package com.memory.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTimeEntity {

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private LocalDateTime deleteDate;

    public void updateDelete() {
        this.deleteDate = LocalDateTime.now();
    }
}
