package com.memory.domain.calendar;

import com.memory.domain.member.Member;
import com.memory.exception.customException.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Entity
@Getter
@DiscriminatorValue("PERSONAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalEvent extends BaseCalendarEvent {

    private PersonalEvent(String title, String description, LocalDateTime startDateTime, 
                         LocalDateTime endDateTime, String location, Member member) {
        super(title, description, startDateTime, endDateTime, location, member);
    }

    // 개인 일정 생성 팩토리 메서드
    public static PersonalEvent create(String title, String description, 
                                     LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                     String location, Member member) {
        return new PersonalEvent(title, description, startDateTime, endDateTime, 
                                location, member);
    }

    // 일정 접근 권한 확인 메서드 (개인 일정은 소유자만 접근 가능)
    @Override
    public void validateAccessPermission(Member member) {
        if (isOwner(member)) {
            throw new ValidationException("이 일정에 접근할 권한이 없습니다.");
        }
    }
}
