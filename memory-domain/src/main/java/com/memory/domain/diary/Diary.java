package com.memory.domain.diary;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate date;

    private String mood;

    private String weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Diary(String title, String content, LocalDate date, String mood, String weather, Member member) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.mood = mood;
        this.weather = weather;
        this.member = member;
    }

    public static Diary create(String title, String content, LocalDate date, String mood, String weather, Member member) {
        return new Diary(title, content, date, mood, weather, member);
    }

    public void update(String title, String content, LocalDate date, String mood, String weather) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.mood = mood;
        this.weather = weather;
    }

    public boolean isOwner(Member member) {
        return !this.member.getId().equals(member.getId());
    }
}