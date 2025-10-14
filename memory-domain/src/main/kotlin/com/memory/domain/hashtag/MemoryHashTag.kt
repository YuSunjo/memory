package com.memory.domain.hashtag

import com.memory.domain.BaseTimeEntity
import com.memory.domain.memory.Memory
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class MemoryHashTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    var memory: Memory? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_tag_id")
    var hashTag: HashTag? = null
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(memory: Memory, hashTag: HashTag): MemoryHashTag =
            MemoryHashTag(
                memory = memory,
                hashTag = hashTag
            )
    }

    fun updateMemory(memory: Memory) {
        this.memory = memory
    }
}