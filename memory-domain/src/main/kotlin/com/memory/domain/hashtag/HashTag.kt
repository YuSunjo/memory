package com.memory.domain.hashtag

import com.memory.domain.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class HashTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String,

    @Column(nullable = false)
    var useCount: Long = 0L
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(name: String): HashTag = HashTag(name = name, useCount = 0L)
    }

    fun incrementUseCount() {
        this.useCount++
    }

    fun decrementUseCount() {
        if (this.useCount > 0) this.useCount--
    }
}