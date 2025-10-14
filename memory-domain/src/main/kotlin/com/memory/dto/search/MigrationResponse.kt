package com.memory.dto.search

data class MigrationResponse(
    val success: Boolean,
    val message: String,
    val processedCount: Long,
    val successCount: Long,
    val errorCount: Long,
    val elapsedTimeMs: Long
) {
    companion object {
        @JvmStatic
        fun success(
            message: String,
            processedCount: Long,
            successCount: Long,
            errorCount: Long,
            elapsedTimeMs: Long
        ): MigrationResponse {
            return MigrationResponse(
                success = true,
                message = message,
                processedCount = processedCount,
                successCount = successCount,
                errorCount = errorCount,
                elapsedTimeMs = elapsedTimeMs
            )
        }

        @JvmStatic
        fun failure(
            message: String,
            processedCount: Long,
            successCount: Long,
            errorCount: Long,
            elapsedTimeMs: Long
        ): MigrationResponse {
            return MigrationResponse(
                success = false,
                message = message,
                processedCount = processedCount,
                successCount = successCount,
                errorCount = errorCount,
                elapsedTimeMs = elapsedTimeMs
            )
        }
    }
}
