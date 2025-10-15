package com.memory.annotation

/**
 * Annotation to inject the memberId extracted from the JWT token.
 * This annotation can be used on method parameters to receive the memberId.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class MemberId 