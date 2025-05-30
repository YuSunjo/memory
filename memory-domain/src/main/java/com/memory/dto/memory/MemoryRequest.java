package com.memory.dto.memory;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.map.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MemoryRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @NotBlank(message = "위치 이름은 필수 입력값입니다.")
        private String locationName;

        @NotNull(message = "지도 ID는 필수 입력값입니다.")
        private Long mapId;

        public Create(String title, String content, String locationName, Long mapId) {
            this.title = title;
            this.content = content;
            this.locationName = locationName;
            this.mapId = mapId;
        }

        public Memory toEntity(Member member, Map map) {
            return new Memory(title, content, locationName, member, map);
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

        public Update(String title, String content, String locationName) {
            this.title = title;
            this.content = content;
            this.locationName = locationName;
        }
    }

    @Getter
    public static class GetByMember {
        private Long lastMemoryId;
        private Integer size;

        public GetByMember(Long lastMemoryId, Integer size) {
            this.lastMemoryId = lastMemoryId;
            this.size = size;
        }
    }

    @Getter
    public static class GetByMap {
        private Long mapId;
        private Long lastMemoryId;
        private Integer size;

        public GetByMap(Long mapId, Long lastMemoryId, Integer size) {
            this.mapId = mapId;
            this.lastMemoryId = lastMemoryId;
            this.size = size;
        }
    }
}
