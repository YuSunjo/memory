package com.memory.dto.diary.response;

import com.memory.domain.diary.Diary;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class DiaryResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDate date;
    private final String mood;
    private final String weather;
    private final MemberResponse member;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    private DiaryResponse(Long id, String title, String content, LocalDate date, 
                         String mood, String weather, MemberResponse member, 
                         LocalDateTime createDate, LocalDateTime updateDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.mood = mood;
        this.weather = weather;
        this.member = member;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public static DiaryResponse from(Diary diary) {
        if (diary == null) {
            return null;
        }

        return new DiaryResponse(
            diary.getId(),
            diary.getTitle(),
            diary.getContent(),
            diary.getDate(),
            diary.getMood(),
            diary.getWeather(),
            MemberResponse.from(diary.getMember()),
            diary.getCreateDate(),
            diary.getUpdateDate()
        );
    }
}