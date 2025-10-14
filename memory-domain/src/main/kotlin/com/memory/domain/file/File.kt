package com.memory.domain.file

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import jakarta.persistence.*

@Entity
@Table(name = "file")
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var originalFileName: String,

    var fileName: String,

    var fileUrl: String,

    @Enumerated(EnumType.STRING)
    var fileType: FileType,

    var fileSize: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    var memory: Memory? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null
) : BaseTimeEntity() {

    constructor(originalFileName: String, fileName: String, fileUrl: String, fileType: FileType, fileSize: Long) : this(
        id = null,
        originalFileName = originalFileName,
        fileName = fileName,
        fileUrl = fileUrl,
        fileType = fileType,
        fileSize = fileSize
    )

    fun updateMemory(memory: Memory?) {
        this.memory = memory
    }

    fun updateMember(member: Member?) {
        this.member = member
    }

    fun validateMember(memberId: Long): Boolean {
        return this.member != null && this.member!!.id != memberId
    }
}
