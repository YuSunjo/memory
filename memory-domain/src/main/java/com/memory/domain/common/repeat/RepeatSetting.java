package com.memory.domain.common.repeat;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatSetting {
    
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;
    
    private Integer interval; // 반복 간격 (예: 2주마다, 3일마다)
    
    private LocalDate startDate; // 반복 시작일
    
    private LocalDate endDate; // 반복 종료일 (null이면 무한 반복)
    
    public RepeatSetting(RepeatType repeatType, Integer interval, LocalDate startDate, LocalDate endDate) {
        this.repeatType = repeatType;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // 반복 없음 설정을 위한 정적 팩토리 메서드
    public static RepeatSetting none() {
        return new RepeatSetting(RepeatType.NONE, null, null, null);
    }
    
    // 반복 설정을 위한 정적 팩토리 메서드
    public static RepeatSetting of(RepeatType repeatType, Integer interval, LocalDate startDate, LocalDate endDate) {
        if (repeatType == RepeatType.NONE) {
            return none();
        }
        return new RepeatSetting(repeatType, interval, startDate, endDate);
    }
    
    // 특정 날짜에 이 설정이 적용되는지 확인
    public boolean isApplicableOn(LocalDate targetDate) {
        if (repeatType == RepeatType.NONE || startDate == null) {
            return false;
        }
        
        // 시작일 이전이면 적용되지 않음
        if (targetDate.isBefore(startDate)) {
            return false;
        }
        
        // 종료일이 설정되어 있고 종료일 이후면 적용되지 않음
        if (endDate != null && targetDate.isAfter(endDate)) {
            return false;
        }
        
        // 반복 타입별 로직
        switch (repeatType) {
            case DAILY:
                long daysBetween = ChronoUnit.DAYS.between(startDate, targetDate);
                return daysBetween % (interval != null ? interval : 1) == 0;
                
            case WEEKLY:
                // 같은 요일이고, 주 간격이 맞는지 확인
                if (targetDate.getDayOfWeek() != startDate.getDayOfWeek()) {
                    return false;
                }
                long weeksBetween = ChronoUnit.WEEKS.between(startDate, targetDate);
                return weeksBetween % (interval != null ? interval : 1) == 0;
                
            case MONTHLY:
                // 같은 일자이고, 월 간격이 맞는지 확인
                if (targetDate.getDayOfMonth() != startDate.getDayOfMonth()) {
                    return false;
                }
                long monthsBetween = ChronoUnit.MONTHS.between(startDate, targetDate);
                return monthsBetween % (interval != null ? interval : 1) == 0;
                
            case YEARLY:
                // 같은 월, 일이고, 년 간격이 맞는지 확인
                if (targetDate.getMonthValue() != startDate.getMonthValue() || 
                    targetDate.getDayOfMonth() != startDate.getDayOfMonth()) {
                    return false;
                }
                long yearsBetween = ChronoUnit.YEARS.between(startDate, targetDate);
                return yearsBetween % (interval != null ? interval : 1) == 0;
                
            default:
                return false;
        }
    }
}