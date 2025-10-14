package com.memory.annotation.swagger

import com.memory.response.ServerResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlin.reflect.KClass

object ApiOperations {

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ServerResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @Operation(
        summary = "#{#summary}",
        description = "#{#description}"
    )
    annotation class BasicApi(
        val summary: String,
        val description: String = "",
        val response: KClass<*> = Void::class
    )

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ServerResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패"),
            ApiResponse(responseCode = "403", description = "권한 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    @Operation(
        summary = "#{#summary}",
        description = "#{#description}"
    )
    annotation class SecuredApi(
        val summary: String,
        val description: String = "",
        val response: KClass<*> = Void::class
    )
}
