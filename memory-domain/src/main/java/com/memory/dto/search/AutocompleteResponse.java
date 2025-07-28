package com.memory.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AutocompleteResponse {
    
    private List<AutocompleteSuggestion> suggestions;
    private long responseTimeMs;
    private String query;
    private int totalSuggestions;
}