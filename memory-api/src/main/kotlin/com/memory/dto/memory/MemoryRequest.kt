package com.memory.dto.memory;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.map.Map;
import com.memory.domain.memory.MemoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemoryRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @NotBlank(message = "위치 이름은 필수 입력값입니다.")
        private String locationName;

        @NotNull(message = "메모리 날짜는 필수 입력값입니다.")
        private LocalDate memorableDate;

        @NotNull(message = "지도 ID는 필수 입력값입니다.")
        private Long mapId;

        @NotNull(message = "메모리 타입은 필수 입력값입니다.(PUBLIC, PRIVATE, COUPLE_PUBLIC, COUPLE_PRIVATE)")
        private MemoryType memoryType;

        private List<Long> fileIdList;

        private List<String> hashTagList;

        public Create(String title, String content, String locationName, LocalDate memorableDate, Long mapId, MemoryType memoryType, List<Long> fileIdList, List<String> hashTagList) {
            this.title = title;
            this.content = content;
            this.locationName = locationName;
            this.memorableDate = memorableDate;
            this.mapId = mapId;
            this.memoryType = memoryType;
            this.fileIdList = fileIdList != null ? fileIdList : new ArrayList<>();
            this.hashTagList = hashTagList != null ? hashTagList : new ArrayList<>();
        }

        public Memory toEntity(Member member, Map map) {
            return new Memory(title, content, locationName, memorableDate, memoryType, member, map);
        }
    }

    @Getter
    public static class Update {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @NotBlank(message = "위치 이름은 필수 입력값입니다.")
        private String locationName;

        private LocalDate memorableDate;

        @NotNull(message = "메모리 타입은 필수 입력값입니다.(PUBLIC, PRIVATE, COUPLE_PUBLIC, COUPLE_PRIVATE)")
        private MemoryType memoryType;

        private List<Long> fileIdList;

        private List<String> hashTagList;

        public Update(String title, String content, String locationName, LocalDate memorableDate, MemoryType memoryType, List<Long> fileIdList, List<String> hashTagList) {
            this.title = title;
            this.content = content;
            this.locationName = locationName;
            this.memorableDate = memorableDate;
            this.memoryType = memoryType;
            this.fileIdList = fileIdList != null ? fileIdList : new ArrayList<>();
            this.hashTagList = hashTagList != null ? hashTagList : new ArrayList<>();
        }
    }

    @Getter
    public static class GetByMember {
        private Long lastMemoryId;
        private Integer size;
        private MemoryType memoryType;

        public GetByMember(Long lastMemoryId, Integer size, MemoryType memoryType) {
            this.lastMemoryId = lastMemoryId;
            this.size = size != null ? size : 10;
            this.memoryType = memoryType != null ? memoryType : MemoryType.PUBLIC;
        }
    }

    @Getter
    public static class GetByPublic {
        private Long lastMemoryId;
        private Integer size;

        public GetByPublic(Long lastMemoryId, Integer size) {
            this.lastMemoryId = lastMemoryId;
            this.size = size != null ? size : 10;
        }
    }
}
