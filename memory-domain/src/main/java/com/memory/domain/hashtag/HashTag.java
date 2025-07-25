package com.memory.domain.hashtag;

import com.memory.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hash_tag")
public class HashTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long useCount = 0L;

    public HashTag(String name) {
        this.name = name;
        this.useCount = 0L;
    }

    public static HashTag create(String name) {
        return new HashTag(name);
    }

    public void incrementUseCount() {
        this.useCount++;
    }

    public void decrementUseCount() {
        if (this.useCount > 0) {
            this.useCount--;
        }
    }
}