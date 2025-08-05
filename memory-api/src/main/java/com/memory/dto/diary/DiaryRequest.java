package com.memory.dto.diary;

import com.memory.domain.diary.Diary;
import com.memory.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

public class DiaryRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "날짜는 필수 입력값입니다.")
        private LocalDate date;

        private final String mood;

        private final String weather;

        public Create(String title, String content, LocalDate date, String mood, String weather) {
            this.title = title;
            this.content = content;
            this.date = date;
            this.mood = mood;
            this.weather = weather;
        }

        public Diary toEntity(Member member) {
            return Diary.create(title, content, date, mood, weather, member);
        }
    }

    @Getter
    public static class Update {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "날짜는 필수 입력값입니다.")
        private LocalDate date;

        private final String mood;

        private final String weather;

        public Update(String title, String content, LocalDate date, String mood, String weather) {
            this.title = title;
            this.content = content;
            this.date = date;
            this.mood = mood;
            this.weather = weather;
        }
    }

    @Getter
    public static class GetByDateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public GetByDateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}