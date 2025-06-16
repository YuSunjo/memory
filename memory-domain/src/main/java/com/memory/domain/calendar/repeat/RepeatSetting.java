package com.memory.domain.calendar.repeat;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatSetting {
    
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;
    
    private Integer interval; // 반복 간격 (예: 2주마다, 3일마다)
    
    private LocalDate endDate; // 반복 종료일 (null이면 무한 반복)
    
    public RepeatSetting(RepeatType repeatType, Integer interval, LocalDate endDate) {
        this.repeatType = repeatType;
        this.interval = interval;
        this.endDate = endDate;
    }
    
    // 반복 없음 설정을 위한 정적 팩토리 메서드
    public static RepeatSetting none() {
        return new RepeatSetting(RepeatType.NONE, null, null);
    }
    
    // 반복 설정을 위한 정적 팩토리 메서드
    public static RepeatSetting of(RepeatType repeatType, Integer interval, LocalDate endDate) {
        if (repeatType == RepeatType.NONE) {
            return none();
        }
        return new RepeatSetting(repeatType, interval, endDate);
    }

}