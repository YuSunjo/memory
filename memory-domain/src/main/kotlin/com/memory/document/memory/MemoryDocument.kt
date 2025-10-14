package com.memory.document.memory

import com.memory.domain.file.File
import com.memory.domain.memory.Memory
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import org.springframework.data.elasticsearch.annotations.Setting
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Document(indexName = "memory")
@Setting(settingPath = "elasticsearch/memory-settings.json")
data class MemoryDocument(

    @Id
    var id: String? = null,

    @Field(type = FieldType.Long)
    var memoryId: Long? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var title: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var content: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var locationName: String? = null,

    @Field(type = FieldType.Date, format = [DateFormat.date])
    var memorableDate: LocalDate? = null,

    @Field(type = FieldType.Text, analyzer = "nori_with_html_strip")
    var memorableDateText: String? = null,

    @Field(type = FieldType.Keyword)
    var memoryType: String? = null,

    @Field(type = FieldType.Keyword)
    var hashTags: List<String>? = null,

    @Field(type = FieldType.Long)
    var memberId: Long? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var memberName: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var memberNickname: String? = null,

    @Field(type = FieldType.Keyword)
    var memberEmail: String? = null,

    @Field(type = FieldType.Keyword)
    var memberFileUrl: String? = null,

    @Field(type = FieldType.Long)
    var relationshipMemberId: Long? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var relationshipMemberName: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = [InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")]
    )
    var relationshipMemberNickname: String? = null,

    @Field(type = FieldType.Keyword)
    var relationshipMemberEmail: String? = null,

    @Field(type = FieldType.Keyword)
    var relationshipMemberFileUrl: String? = null
) {

    companion object {

        @JvmStatic
        fun from(memory: Memory, relationships: RelationshipInfo): MemoryDocument {
            val member = memory.member

            return MemoryDocument(
                memoryId = memory.id,
                title = memory.title,
                content = memory.content,
                locationName = memory.locationName,
                memorableDate = memory.memorableDate,
                memorableDateText = formatMemorableDate(memory.memorableDate),
                memoryType = memory.memoryType.name,
                hashTags = memory.getHashTagNames(),
                memberId = member.id,
                memberName = member.name,
                memberNickname = member.nickname,
                memberEmail = member.email,
                memberFileUrl = extractFileUrl(member.file),
                relationshipMemberId = relationships.firstId(),
                relationshipMemberName = relationships.firstName(),
                relationshipMemberNickname = relationships.firstNickname(),
                relationshipMemberEmail = relationships.firstEmail(),
                relationshipMemberFileUrl = relationships.firstFileUrl()
            )
        }

        private fun formatMemorableDate(date: LocalDate?): String? {
            if (date == null) return null
            val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN)
            return date.format(formatter)
        }

        private fun extractFileUrl(file: File?): String? =
            file?.fileUrl
    }

    fun updateFromMemory(memory: Memory, relationships: RelationshipInfo) {
        val member = memory.member

        this.memoryId = memory.id
        this.title = memory.title
        this.content = memory.content
        this.locationName = memory.locationName
        this.memorableDate = memory.memorableDate
        this.memorableDateText = formatMemorableDate(memory.memorableDate)
        this.memoryType = memory.memoryType.name
        this.hashTags = memory.getHashTagNames()

        this.memberId = member.id
        this.memberName = member.name
        this.memberNickname = member.nickname
        this.memberEmail = member.email
        this.memberFileUrl = extractFileUrl(member.file)

        this.relationshipMemberId = relationships.firstId()
        this.relationshipMemberName = relationships.firstName()
        this.relationshipMemberNickname = relationships.firstNickname()
        this.relationshipMemberEmail = relationships.firstEmail()
        this.relationshipMemberFileUrl = relationships.firstFileUrl()
    }
}