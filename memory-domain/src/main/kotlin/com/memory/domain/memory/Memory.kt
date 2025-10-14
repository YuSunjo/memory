package com.memory.domain.memory

import com.memory.domain.BaseTimeEntity
import com.memory.domain.comment.Comment
import com.memory.domain.file.File
import com.memory.domain.hashtag.MemoryHashTag
import com.memory.domain.map.Map
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "memory")
class Memory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var title: String,

    @Column(columnDefinition = "text")
    var content: String?,

    var locationName: String?,

    var memorableDate: LocalDate?,

    @Enumerated(EnumType.STRING)
    var memoryType: MemoryType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    var map: Map?,

    @OneToMany(mappedBy = "memory", cascade = [CascadeType.ALL], orphanRemoval = true)
    var files: MutableList<File> = mutableListOf(),

    @OneToMany(mappedBy = "memory", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "memory", cascade = [CascadeType.ALL], orphanRemoval = true)
    var memoryHashTags: MutableList<MemoryHashTag> = mutableListOf()
) : BaseTimeEntity() {

    // JPA용 프라이빗 생성자
    constructor(title: String, content: String?, locationName: String?, memorableDate: LocalDate?, memoryType: MemoryType, member: Member, map: Map) : this(
        id = null,
        title = title,
        content = content,
        locationName = locationName,
        memorableDate = memorableDate,
        memoryType = memoryType,
        member = member,
        map = null
    )

    fun update(
        title: String,
        content: String?,
        locationName: String?,
        memorableDate: LocalDate?,
        memoryType: MemoryType
    ) {
        this.title = title
        this.content = content
        this.locationName = locationName
        this.memorableDate = memorableDate
        this.memoryType = memoryType
    }

    fun addFile(file: File) {
        this.files.add(file)
        file.updateMemory(this)
    }

    fun addFiles(files: List<File>) {
        files.forEach { addFile(it) }
    }

    fun addComment(comment: Comment) {
        this.comments.add(comment)
    }

    fun addMemoryHashTag(memoryHashTag: MemoryHashTag) {
        this.memoryHashTags.add(memoryHashTag)
        memoryHashTag.updateMemory(this)
    }

    fun addMemoryHashTags(memoryHashTags: List<MemoryHashTag>) {
        memoryHashTags.forEach { addMemoryHashTag(it) }
    }

    fun clearHashTags() {
        this.memoryHashTags.clear()
    }

    fun getHashTagNames(): List<String> {
        return memoryHashTags
            .filter { !it.isDeleted() }
            .map { it.hashTag?.name ?: "" }
    }

    fun getTopLevelCommentsCount(): Long {
        return comments
            .filter { !it.isDeleted() && it.isTopLevel() }
            .count()
            .toLong()
    }

    fun getCommentsCount(): Long {
        return comments
            .filter { !it.isDeleted() }
            .count()
            .toLong()
    }

    fun isPublic(): Boolean {
        return memoryType == MemoryType.PUBLIC
    }
}
