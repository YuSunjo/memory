package com.memory.document.memory;

import com.memory.domain.file.File;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import com.memory.dto.relationship.response.RelationshipListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "memory")
@Setting(settingPath = "elasticsearch/memory-settings.json")
public class MemoryDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long memoryId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String title;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String content;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String locationName;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate memorableDate;

    @Field(type = FieldType.Text, analyzer = "nori_with_html_strip")
    private String memorableDateText;

    @Field(type = FieldType.Keyword)
    private String memoryType;

    @Field(type = FieldType.Keyword)
    private List<String> hashTags;

    @Field(type = FieldType.Long)
    private Long memberId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String memberName;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String memberNickname;

    @Field(type = FieldType.Keyword)
    private String memberEmail;

    @Field(type = FieldType.Keyword)
    private String memberFileUrl;

    @Field(type = FieldType.Long)
    private Long relationshipMemberId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String relationshipMemberName;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_with_html_strip"),
        otherFields = {
            @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
        }
    )
    private String relationshipMemberNickname;

    @Field(type = FieldType.Keyword)
    private String relationshipMemberEmail;

    @Field(type = FieldType.Keyword)
    private String relationshipMemberFileUrl;

    public static MemoryDocument from(Memory memory, RelationshipListResponse relationships) {
        Member member = memory.getMember();
        
        return MemoryDocument.builder()
                .memoryId(memory.getId())
                .title(memory.getTitle())
                .content(memory.getContent())
                .locationName(memory.getLocationName())
                .memorableDate(memory.getMemorableDate())
                .memorableDateText(formatMemorableDate(memory.getMemorableDate()))
                .memoryType(memory.getMemoryType().name())
                .hashTags(memory.getHashTagNames())
                .memberId(member.getId())
                .memberName(member.getName())
                .memberNickname(member.getNickname())
                .memberEmail(member.getEmail())
                .memberFileUrl(extractFileUrl(member.getFile()))
                .relationshipMemberId(extractRelationshipMember(relationships, RelationshipMemberField.ID))
                .relationshipMemberName(extractRelationshipMember(relationships, RelationshipMemberField.NAME))
                .relationshipMemberNickname(extractRelationshipMember(relationships, RelationshipMemberField.NICKNAME))
                .relationshipMemberEmail(extractRelationshipMember(relationships, RelationshipMemberField.EMAIL))
                .relationshipMemberFileUrl(extractRelationshipMember(relationships, RelationshipMemberField.FILE_URL))
                .build();
    }

    private static String formatMemorableDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN);
        return date.format(formatter);
    }

    private static String extractFileUrl(File file) {
        return file != null ? file.getFileUrl() : null;
    }

    private enum RelationshipMemberField {
        ID, NAME, NICKNAME, EMAIL, FILE_URL
    }

    @SuppressWarnings("unchecked")
    private static <T> T extractRelationshipMember(RelationshipListResponse relationships, RelationshipMemberField field) {
        if (relationships == null || relationships.relationships() == null || relationships.relationships().isEmpty()) {
            return null;
        }
        
        var relatedMember = relationships.relationships().get(0).relatedMember();
        return switch (field) {
            case ID -> (T) relatedMember.id();
            case NAME -> (T) relatedMember.name();
            case NICKNAME -> (T) relatedMember.nickname();
            case EMAIL -> (T) relatedMember.email();
            case FILE_URL -> relatedMember.profile() != null ? (T) relatedMember.profile().fileUrl() : null;
        };
    }

    public void updateFromMemory(Memory memory, RelationshipListResponse relationships) {
        Member member = memory.getMember();
        
        this.memoryId = memory.getId();
        this.title = memory.getTitle();
        this.content = memory.getContent();
        this.locationName = memory.getLocationName();
        this.memorableDate = memory.getMemorableDate();
        this.memorableDateText = formatMemorableDate(memory.getMemorableDate());
        this.memoryType = memory.getMemoryType().name();
        this.hashTags = memory.getHashTagNames();
        
        this.memberId = member.getId();
        this.memberName = member.getName();
        this.memberNickname = member.getNickname();
        this.memberEmail = member.getEmail();
        this.memberFileUrl = extractFileUrl(member.getFile());
        
        this.relationshipMemberId = extractRelationshipMember(relationships, RelationshipMemberField.ID);
        this.relationshipMemberName = extractRelationshipMember(relationships, RelationshipMemberField.NAME);
        this.relationshipMemberNickname = extractRelationshipMember(relationships, RelationshipMemberField.NICKNAME);
        this.relationshipMemberEmail = extractRelationshipMember(relationships, RelationshipMemberField.EMAIL);
        this.relationshipMemberFileUrl = extractRelationshipMember(relationships, RelationshipMemberField.FILE_URL);
    }

}