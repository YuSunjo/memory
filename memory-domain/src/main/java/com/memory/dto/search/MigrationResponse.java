package com.memory.dto.search;

import lombok.Getter;

@Getter
public class MigrationResponse {
    
    private final boolean success;
    private final String message;
    private final long processedCount;
    private final long successCount;
    private final long errorCount;
    private final long elapsedTimeMs;

    public MigrationResponse(boolean success, String message, long processedCount, long successCount, long errorCount, long elapsedTimeMs) {
        this.success = success;
        this.message = message;
        this.processedCount = processedCount;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.elapsedTimeMs = elapsedTimeMs;
    }

    public static MigrationResponse success(String message, long processedCount,
                                            long successCount, long errorCount, long elapsedTimeMs) {
        return new MigrationResponse(true, message, processedCount, successCount, errorCount, elapsedTimeMs);
    }
    
    public static MigrationResponse failure(String message, long processedCount, 
                                          long successCount, long errorCount, long elapsedTimeMs) {
        return new MigrationResponse(false, message, processedCount, successCount, errorCount, elapsedTimeMs);
    }
}