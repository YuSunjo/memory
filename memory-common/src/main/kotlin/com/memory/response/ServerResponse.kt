package com.memory.response

data class ServerResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T?
) {
    companion object {
        @JvmField
        val OK = ServerResponse(200, "OK", null)

        @JvmStatic
        fun <T> success(data: T): ServerResponse<T> {
            return ServerResponse(200, "OK", data)
        }

        @JvmStatic
        fun error(statusCode: Int, message: String): ServerResponse<Any?> {
            return ServerResponse(statusCode, message, null)
        }
    }
}
