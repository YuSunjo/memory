package com.memory.dto.map;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MapRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "지도 이름은 필수 입력값입니다.")
        private String name;

        private String description;

        @NotBlank(message = "주소는 필수 입력값입니다.")
        private String address;

        @NotBlank(message = "위도는 필수 입력값입니다.")
        private String latitude;

        @NotBlank(message = "경도는 필수 입력값입니다.")
        private String longitude;

        @NotNull(message = "지도 타입은 필수 입력값입니다.")
        private MapType mapType;

        public Create(String name, String description, String address, String latitude, String longitude, MapType mapType) {
            this.name = name;
            this.description = description;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.mapType = mapType;
        }

        public Map toEntity() {
            return new Map(name, description, address, latitude, longitude, mapType);
        }
    }
}