package com.memory.domain.routine;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.common.repeat.RepeatSetting;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Routine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private boolean active; // 활성화 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private RepeatSetting repeatSetting;

    @Builder
    private Routine(String title, String content, boolean active, Member member, RepeatSetting repeatSetting) {
        this.title = title;
        this.content = content;
        this.active = active;
        this.member = member;
        this.repeatSetting = repeatSetting != null ? repeatSetting : RepeatSetting.none();
    }

    public static Routine create(String title, String content, Member member, RepeatSetting repeatSetting) {
        return Routine.builder()
                .title(title)
                .content(content)
                .active(true)
                .member(member)
                .repeatSetting(repeatSetting)
                .build();
    }

    public void update(String title, String content, RepeatSetting repeatSetting) {
        this.title = title;
        this.content = content;
        this.repeatSetting = repeatSetting != null ? repeatSetting : RepeatSetting.none();
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isOwner(Member member) {
        if (member == null || this.member == null) {
            return false;
        }
        return this.member.getId().equals(member.getId());
    }

    // 특정 날짜에 이 루틴이 적용되는지 확인
    public boolean isApplicableOn(LocalDateTime dateTime) {
        if (!active) {
            return false;
        }
        return repeatSetting.isApplicableOn(dateTime.toLocalDate());
    }

    // Todo로 변환할 때 사용 (기본 시간 사용)
    public LocalDateTime getDateTimeFor(LocalDateTime targetDate) {
        return targetDate.toLocalDate().atTime(9, 0); // 기본값: 오전 9시
    }
}
