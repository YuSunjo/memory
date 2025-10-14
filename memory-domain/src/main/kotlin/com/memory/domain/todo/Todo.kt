package com.memory.domain.todo

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import com.memory.domain.routine.Routine
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String? = null,

    var content: String? = null,

    var dueDate: LocalDateTime? = null,

    var completed: Boolean = false,

    // 루틴에서 생성된 Todo인지 여부
    @Column(name = "isRoutine")
    var isRoutine: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    var routine: Routine? = null
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(
            title: String,
            content: String,
            dueDate: LocalDateTime,
            member: Member
        ): Todo = Todo(
            title = title,
            content = content,
            dueDate = dueDate,
            completed = false,
            member = member,
            routine = null,
            isRoutine = false
        )

        @JvmStatic
        fun createFromRoutine(
            routine: Routine,
            dueDate: LocalDateTime,
            member: Member
        ): Todo = Todo(
            title = routine.title,
            content = routine.content,
            dueDate = dueDate,
            completed = false,
            member = member,
            routine = routine,
            isRoutine = true
        )
    }

    fun update(title: String, content: String, dueDate: LocalDateTime) {
        this.title = title
        this.content = content
        this.dueDate = dueDate
    }

    fun complete() {
        this.completed = true
    }

    fun incomplete() {
        this.completed = false
    }

    fun isOwner(member: Member?): Boolean {
        val me = this.member ?: return false
        val other = member ?: return false
        return me.id == other.id
    }

    fun isFromRoutine(): Boolean = isRoutine && routine != null
}