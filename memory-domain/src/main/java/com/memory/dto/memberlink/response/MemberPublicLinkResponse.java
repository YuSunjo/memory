package com.memory.dto.memberlink.response;

import com.memory.dto.member.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberPublicLinkResponse {

    private List<MemberLinkResponse> memberLinks;
    private MemberResponse member;

    public static MemberPublicLinkResponse of(List<MemberLinkResponse> memberLinks, MemberResponse member) {
        return new MemberPublicLinkResponse(memberLinks, member);
    }

}
