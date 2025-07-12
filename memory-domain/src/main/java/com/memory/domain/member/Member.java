package com.memory.domain.member;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.file.File;
import com.memory.domain.map.Map;
import com.memory.domain.memberlink.MemberLink;
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

    @OneToOne(mappedBy = "member")
    private File file;

    @OneToOne(mappedBy = "member")
    private Relationship relationship;

    @OneToOne(mappedBy = "relatedMember")
    private Relationship relatedRelationship;    // 내가 받은 관계

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map> maps = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLink> memberLinks = new ArrayList<>();

    public Member(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.memberType = MemberType.MEMBER;
    }

    public Member(String name, String nickname, String email, String password, MemberType memberType) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.memberType = memberType;
    }

    public void update(String nickname, File file) {
        this.nickname = nickname;
        this.updateFile(file);
    }

    // 연관관계 편의 메서드
    private void updateFile(File file) {
        if (this.file != null) {
            this.file.updateMember(null);
        }
        this.file = file;
        file.updateMember(this);
    }

    public void updatePassword(String password) {
        if (password != null) {
            this.password = password;
        }
    }

    // MemberLink 연관관계 편의 메서드
    public void addMemberLink(MemberLink memberLink) {
        this.memberLinks.add(memberLink);
    }
}
