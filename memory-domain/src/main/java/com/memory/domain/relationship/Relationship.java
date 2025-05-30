package com.memory.domain.relationship;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import com.memory.exception.customException.ValidationException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Relationship extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_member_id")
    private Member relatedMember;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus relationshipStatus;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public Relationship(Member member, Member relatedMember, RelationshipStatus relationshipStatus) {
        this.member = member;
        this.relatedMember = relatedMember;
        this.relationshipStatus = relationshipStatus;
        this.startDate = LocalDateTime.now();
    }

    public static Relationship createRelationship(Member member, Member relatedMember, RelationshipStatus relationshipStatus) {
        return new Relationship(member, relatedMember, relationshipStatus);
    }

    public void validateAcceptPermission(Member member) {
        if (!this.getRelatedMember().getId().equals(member.getId())) {
            throw new ValidationException("관계 요청을 수락할 권한이 없습니다.");
        }
    }

    public void validateEndPermission(Member member) {
        if (!this.getMember().getId().equals(member.getId()) && !this.getRelatedMember().getId().equals(member.getId())) {
            throw new ValidationException("관계를 끊을 권한이 없습니다.");
        }
    }

    public void accept() {
        if (this.relationshipStatus != RelationshipStatus.PENDING) {
            throw new ValidationException("Only pending relationships can be accepted");
        }
        this.relationshipStatus = RelationshipStatus.ACCEPTED;
    }

    public void end() {
        if (this.relationshipStatus != RelationshipStatus.ACCEPTED) {
            throw new ValidationException("Only accepted relationships can be ended");
        }
        this.relationshipStatus = RelationshipStatus.ENDED;
        this.endDate = LocalDateTime.now();
    }
}
