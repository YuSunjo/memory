package com.memory.dto.member;

import com.memory.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class Signup {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String name;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        private String nickname;

        private final String profileImageUrl;

        public Signup(String email, String password, String name, String nickname, String profileImageUrl) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
        }

        public Member toEntity() {
            return new Member(name, nickname, email, password, profileImageUrl);
        }
    }

}
