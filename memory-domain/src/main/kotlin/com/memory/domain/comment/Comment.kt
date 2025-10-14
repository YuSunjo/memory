package com.memory.domain.comment

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import jakarta.persistence.*

@Entity
@Table(name = "comment")
class Comment private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(columnDefinition = "text", nullable = false)
    var content: String,

    @Column(nullable = false)
    var depth: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    var memory: Memory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<Comment> = mutableListOf()
) : BaseTimeEntity() {

    init {
        validateDepth()
        parent?.addChild(this)
    }

    companion object {
        @JvmStatic
        fun create(
            content: String,
            memory: Memory,
            member: Member,
            parent: Comment? = null
        ): Comment {
            return Comment(
                content = content,
                memory = memory,
                member = member,
                parent = parent,
                depth = calculateDepth(parent)
            )
        }

        private fun calculateDepth(parent: Comment?): Int {
            return if (parent == null) 0 else parent.depth + 1
        }
    }

    private fun validateDepth() {
        require(depth <= 1) { "댓글은 최대 2단계(대댓글)까지만 가능합니다." }
    }

    fun updateContent(content: String) {
        require(content.isNotBlank()) { "댓글 내용은 필수입니다." }
        this.content = content
    }

    private fun addChild(child: Comment) {
        this.children.add(child)
    }

    fun isTopLevel(): Boolean {
        return depth == 0
    }

    fun canHaveReply(): Boolean {
        return depth < 1
    }

    fun isAuthor(member: Member): Boolean {
        return this.member == member
    }

    fun getActiveChildrenCount(): Long {
        return children.count { !it.isDeleted() }.toLong()
    }

    fun markAsDeleted() {
        if (hasActiveChildren()) {
            this.content = "삭제된 댓글입니다."
        }
        this.updateDelete()
    }

    private fun hasActiveChildren(): Boolean {
        return getActiveChildrenCount() > 0
    }
}
