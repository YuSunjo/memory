package com.memory.dto.hashtag.response

import com.memory.domain.hashtag.HashTag

data class HashTagResponse(
    val id: Long?,
    val name: String?,
    val useCount: Long?
) {
    companion object {
        @JvmStatic
        fun from(hashTag: HashTag): HashTagResponse {
            return HashTagResponse(
                hashTag.id,
                hashTag.name,
                hashTag.useCount
            )
        }
    }
}