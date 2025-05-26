package com.memory.domain.relationship;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
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

}
