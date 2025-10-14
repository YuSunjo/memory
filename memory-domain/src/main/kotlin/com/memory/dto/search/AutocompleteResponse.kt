package com.memory.dto.search

data class AutocompleteResponse(
    val suggestions: List<AutocompleteSuggestion>,
    val responseTimeMs: Long,
    val query: String,
    val totalSuggestions: Int
)
