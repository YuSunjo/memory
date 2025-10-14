package com.memory.domain.common.repeat

enum class RepeatType(
    val description: String,
) {
    NONE("반복 없음"),
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매월"),
    YEARLY("매년")
}