package com.memory.domain.memberlink;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    private Boolean isActive = true;

    private Boolean isVisible = true;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    private Long clickCount = 0L;

    @Column(name = "last_clicked_at")
    private LocalDateTime lastClickedAt;

    @Builder
    public MemberLink(Member member, String title, String url, String description,
                     Integer displayOrder, Boolean isActive, Boolean isVisible,
                     String iconUrl) {
        this.member = member;
        this.title = title;
        this.url = url;
        this.description = description;
        this.displayOrder = displayOrder;
        this.isActive = isActive != null ? isActive : true;
        this.isVisible = isVisible != null ? isVisible : true;
        this.iconUrl = iconUrl;
        this.clickCount = 0L;
    }

    public void update(String title, String url, String description,
                      Integer displayOrder, Boolean isActive, Boolean isVisible,
                      String iconUrl) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.isVisible = isVisible;
        this.iconUrl = iconUrl;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isSameOrder(Integer targetOrder) {
        return this.displayOrder.equals(targetOrder);
    }

    public void incrementClickCount() {
        this.clickCount++;
        this.lastClickedAt = LocalDateTime.now();
    }

    public boolean isAccessible() {
        return this.isActive && this.isVisible;
    }

}
