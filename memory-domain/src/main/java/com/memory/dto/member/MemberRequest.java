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

        public Signup(String email, String password, String name, String nickname) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickname = nickname;
        }

        public Member toEntity(String encodedPassword) {
            return new Member(name, nickname, email, encodedPassword);
        }
    }

    @Getter
    public static class Login {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public Login(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    public static class Update {
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        private String nickname;

        @NotBlank(message = "파일 ID는 필수 입력값입니다.")
        private Long fileId;

        public Update(String nickname, Long fileId) {
            this.nickname = nickname;
            this.fileId = fileId;
        }
    }

    @Getter
    public static class PasswordUpdate {
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public PasswordUpdate(String password) {
            this.password = password;
        }
    }
}
