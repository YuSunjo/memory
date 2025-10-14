package com.memory.domain.comment.repository

import com.memory.domain.comment.Comment
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import java.util.Optional

interface CommentRepositoryCustom {

    fun findCommentsByMemoryWithHierarchy(memory: Memory?): List<Comment>

    fun findTopLevelCommentsByMemory(memory: Memory?, page: Int, size: Int): List<Comment>

    fun findRepliesByParentComment(parentComment: Comment?): List<Comment>

    fun countActiveCommentsByMemory(memory: Memory?): Long

    fun findCommentsByMember(member: Member?, page: Int, size: Int): List<Comment>

    fun findByIdWithMember(commentId: Long?): Optional<Comment>

    fun findRecentComments(limit: Int): List<Comment>

    fun findCommentById(commentId: Long?): Optional<Comment>
}
