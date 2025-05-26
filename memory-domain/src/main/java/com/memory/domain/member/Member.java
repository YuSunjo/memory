package com.memory.domain.member;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.relationship.Relationship;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    private String profileImageUrl;

    @OneToMany(mappedBy = "member")
    private List<Relationship> relationshipList = new ArrayList<>();

    @OneToMany(mappedBy = "relatedMember")
    private List<Relationship> relatedRelationshipList = new ArrayList<>();    // 내가 받은 관계들

    public Member(String name, String nickname, String email, String password, String profileImageUrl) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
    }
}
