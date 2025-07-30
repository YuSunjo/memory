package com.memory.dto.search;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MigrationResponse {
    
    private final boolean success;
    private final String message;
    private final long processedCount;
    private final long successCount;
    private final long errorCount;
    private final long elapsedTimeMs;
    
    public static MigrationResponse success(String message, long processedCount, 
                                          long successCount, long errorCount, long elapsedTimeMs) {
        return MigrationResponse.builder()
                .success(true)
                .message(message)
                .processedCount(processedCount)
                .successCount(successCount)
                .errorCount(errorCount)
                .elapsedTimeMs(elapsedTimeMs)
                .build();
    }
    
    public static MigrationResponse failure(String message, long processedCount, 
                                          long successCount, long errorCount, long elapsedTimeMs) {
        return MigrationResponse.builder()
                .success(false)
                .message(message)
                .processedCount(processedCount)
                .successCount(successCount)
                .errorCount(errorCount)
                .elapsedTimeMs(elapsedTimeMs)
                .build();
    }
}