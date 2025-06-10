package com.memory.domain.memory;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.map.Map;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String locationName;

    @Enumerated(EnumType.STRING)
    private MemoryType memoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    public Memory(String title, String content, String locationName, MemoryType memoryType, Member member, Map map) {
        this.title = title;
        this.content = content;
        this.locationName = locationName;
        this.memoryType = memoryType;
        this.member = member;
        this.map = map;
    }

    public void update(String title, String content, String locationName, MemoryType memoryType) {
        this.title = title;
        this.content = content;
        this.locationName = locationName;
        this.memoryType = memoryType;
    }
}
