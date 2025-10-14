package com.memory.dto.search

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class MemorySearchRequest(
    @field:NotNull
    val type: SearchType,

    val query: String? = null,

    val hashtags: List<String>? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val fromDate: LocalDate? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val toDate: LocalDate? = null,

    @field:Min(0)
    val page: Int = 0,

    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,

    val highlight: Boolean = true
)
