package com.memory.dto.hashtag.response;

import com.memory.domain.hashtag.HashTag;

public record HashTagResponse(
        Long id,
        String name,
        Long useCount
) {

    public static HashTagResponse from(HashTag hashTag) {
        return new HashTagResponse(
                hashTag.getId(),
                hashTag.getName(),
                hashTag.getUseCount()
        );
    }
}