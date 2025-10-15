package com.memory.dto.routine;

import com.memory.domain.common.repeat.RepeatSetting;
import com.memory.domain.common.repeat.RepeatType;
import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class RoutineRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String content;
        private RepeatType repeatType;
        private Integer interval;
        private LocalDate startDate;
        private LocalDate endDate;

        public Routine toEntity(Member member) {
            return Routine.create(
                    this.title,
                    this.content,
                    member,
                    toRepeatSetting()
            );
        }

        public RepeatSetting toRepeatSetting() {
            return RepeatSetting.of(this.repeatType, this.interval, this.startDate, this.endDate);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String title;
        private String content;
        private RepeatType repeatType;
        private Integer interval;
        private LocalDate startDate;
        private LocalDate endDate;

        public RepeatSetting toRepeatSetting() {
            return RepeatSetting.of(this.repeatType, this.interval, this.startDate, this.endDate);
        }
    }
}
