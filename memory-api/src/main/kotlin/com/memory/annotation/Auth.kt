package com.memory.annotation

/**
 * Annotation to mark endpoints that require JWT authentication.
 * When this annotation is present, the JWT token will be validated.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Auth 