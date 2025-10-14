package com.memory.document.memory

class RelationshipInfo(
    private val _relationships: List<RelatedMember>?
) {
    fun relationships(): List<RelatedMember>? = _relationships

    fun firstId(): Long? = relationships()?.firstOrNull()?.id
    fun firstName(): String? = relationships()?.firstOrNull()?.name
    fun firstNickname(): String? = relationships()?.firstOrNull()?.nickname
    fun firstEmail(): String? = relationships()?.firstOrNull()?.email
    fun firstFileUrl(): String? = relationships()?.firstOrNull()?.profileFileUrl
}

class RelatedMember(
    val id: Long?,
    val name: String?,
    val nickname: String?,
    val email: String?,
    val profileFileUrl: String?
)