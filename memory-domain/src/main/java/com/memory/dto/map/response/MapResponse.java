package com.memory.dto.map.response;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;

public record MapResponse(
        Long id,
        String name,
        String description,
        String address,
        String latitude,
        String longitude,
        MapType mapType
) {

    public static MapResponse from(Map map) {
        return new MapResponse(
                map.getId(),
                map.getName(),
                map.getDescription(),
                map.getAddress(),
                map.getLatitude(),
                map.getLongitude(),
                map.getMapType()
        );
    }
}