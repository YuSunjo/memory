package com.memory.annotation

/**
 * Annotation to mark endpoints that require admin privileges.
 * When this annotation is present, the request will be validated for admin access.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Admin 