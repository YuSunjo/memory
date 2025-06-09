package com.memory.domain.member;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.map.Map;
import com.memory.domain.relationship.Relationship;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(mappedBy = "member")
    private Relationship relationship;

    @OneToOne(mappedBy = "relatedMember")
    private Relationship relatedRelationship;    // 내가 받은 관계

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map> maps = List.of();

    public Member(String name, String nickname, String email, String password, String profileImageUrl) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.memberType = MemberType.MEMBER;
    }

    public Member(String name, String nickname, String email, String password, String profileImageUrl, MemberType memberType) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.memberType = memberType;
    }

    public void update(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void updatePassword(String password) {
        if (password != null) {
            this.password = password;
        }
    }
}
