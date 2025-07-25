package com.memory.domain.hashtag;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.memory.Memory;
import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = {"memory", "hashTag"})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memory_hash_tag")
public class MemoryHashTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    private Memory memory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_tag_id")
    private HashTag hashTag;

    public MemoryHashTag(Memory memory, HashTag hashTag) {
        this.memory = memory;
        this.hashTag = hashTag;
    }

    public static MemoryHashTag create(Memory memory, HashTag hashTag) {
        return new MemoryHashTag(memory, hashTag);
    }

    public void updateMemory(Memory memory) {
        this.memory = memory;
    }
}