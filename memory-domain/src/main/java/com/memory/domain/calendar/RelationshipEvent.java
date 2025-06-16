package com.memory.domain.calendar;

import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
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
@DiscriminatorValue("RELATIONSHIP_EVENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelationshipEvent extends BaseCalendarEvent {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    private RelationshipEvent(String title, String description, LocalDateTime startDateTime, 
                      LocalDateTime endDateTime, String location, Member member, 
                      Relationship relationship) {
        super(title, description, startDateTime, endDateTime, location, member);
        this.relationship = relationship;
    }

    // 관계 일정 생성 팩토리 메서드
    public static RelationshipEvent create(String title, String description, 
                                  LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                  String location, Member member, Relationship relationship) {
        if (relationship == null) {
            throw new ValidationException("관계 일정은 관계가 필요합니다.");
        }
        return new RelationshipEvent(title, description, startDateTime, endDateTime, 
                             location, member, relationship);
    }

    public boolean isRelatedTo(Member member) {
        return this.relationship != null && 
               (this.relationship.getRelatedMember().getId().equals(member.getId()) || 
                this.relationship.getMember().getId().equals(member.getId()));
    }

    @Override
    public void validateAccessPermission(Member member) {
        if (!isOwnedBy(member) && !isRelatedTo(member)) {
            throw new ValidationException("이 일정에 접근할 권한이 없습니다.");
        }
    }
}
