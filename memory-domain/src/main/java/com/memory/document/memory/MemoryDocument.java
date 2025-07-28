package com.memory.document.memory;

import com.memory.domain.memory.Memory;
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

    @Field(type = FieldType.Text, analyzer = "nori_with_html_strip")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori_with_html_strip")
    private String content;

    @Field(type = FieldType.Text, analyzer = "nori_with_html_strip")
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

    @Field(type = FieldType.Long)
    private Long relationshipMemberId;

    public static MemoryDocument from(Memory memory) {
        return MemoryDocument.builder()
                .memoryId(memory.getId())
                .title(memory.getTitle())
                .content(memory.getContent())
                .locationName(memory.getLocationName())
                .memorableDate(memory.getMemorableDate())
                .memorableDateText(formatMemorableDate(memory.getMemorableDate()))
                .memoryType(memory.getMemoryType().name())
                .memberId(memory.getMember().getId())
                .hashTags(memory.getHashTagNames())
                .relationshipMemberId(extractRelationshipMemberId(memory))
                .build();
    }

    private static String formatMemorableDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN);
        return date.format(formatter);
    }

    private static Long extractRelationshipMemberId(Memory memory) {
        // TODO: Memory와 관련된 관계 멤버 ID 추출 로직
        // 현재는 null 반환, 추후 관계 테이블과 연동하여 구현
        return null;
    }

    public void updateFromMemory(Memory memory) {
        this.memoryId = memory.getId();
        this.title = memory.getTitle();
        this.content = memory.getContent();
        this.locationName = memory.getLocationName();
        this.memorableDate = memory.getMemorableDate();
        this.memorableDateText = formatMemorableDate(memory.getMemorableDate());
        this.memoryType = memory.getMemoryType().name();
        this.hashTags = memory.getHashTagNames();
        this.relationshipMemberId = extractRelationshipMemberId(memory);
    }

}