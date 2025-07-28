package com.memory.dto.search;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemorySearchRequest {

    @NotNull
    private SearchType type;

    private String query;

    private List<String> hashtags;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    @Builder.Default
    @Min(0)
    private int page = 0;

    @Builder.Default
    @Min(1)
    @Max(100)
    private int size = 20;

    @Builder.Default
    private boolean highlight = true;
}