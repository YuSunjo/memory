package com.memory.response

data class ServerResponseKT<T>(
    val statusCode: Int,
    val message: String,
    val data: T?
) {
    companion object {
        val OK = ServerResponseKT(200, "OK", null)

        fun <T> success(data: T): ServerResponseKT<T> {
            return ServerResponseKT(200, "OK", data)
        }

        fun error(statusCode: Int, message: String): ServerResponseKT<Any?> {
            return ServerResponseKT(statusCode, message, null)
        }
    }
}
