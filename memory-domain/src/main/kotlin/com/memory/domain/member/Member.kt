package com.memory.domain.member

import com.memory.domain.BaseTimeEntity
import com.memory.domain.file.File
import com.memory.domain.map.Map
import com.memory.domain.memberlink.MemberLink
import com.memory.domain.relationship.Relationship
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    var nickname: String,

    var email: String,

    var password: String,

    @Enumerated(EnumType.STRING)
    var memberType: MemberType = MemberType.MEMBER,

    @OneToOne(mappedBy = "member")
    var file: File? = null,

    @OneToOne(mappedBy = "member")
    var relationship: Relationship? = null,

    @OneToOne(mappedBy = "relatedMember")
    var relatedRelationship: Relationship? = null,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var maps: MutableList<Map> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var memberLinks: MutableList<MemberLink> = mutableListOf()
) : BaseTimeEntity() {

    constructor(name: String, nickname: String, email: String, password: String) : this(
        id = null,
        name = name,
        nickname = nickname,
        email = email,
        password = password
    )

    fun update(nickname: String, file: File?) {
        this.nickname = nickname
        file?.let { updateFile(it) }
    }

    private fun updateFile(file: File) {
        this.file?.updateMember(null)
        this.file = file
        file.updateMember(this)
    }

    fun updatePassword(password: String?) {
        password?.let { this.password = it }
    }

    fun addMemberLink(memberLink: MemberLink) {
        this.memberLinks.add(memberLink)
    }
}
