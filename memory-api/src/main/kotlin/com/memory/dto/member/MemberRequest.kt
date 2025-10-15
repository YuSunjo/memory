package com.memory.dto.member

import com.memory.domain.member.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class MemberRequest {

    data class Signup(
        @field:NotBlank(message = "이메일은 필수 입력값입니다.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
        val password: String,

        @field:NotBlank(message = "이름은 필수 입력값입니다.")
        val name: String,

        @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
        val nickname: String
    ) {
        fun toEntity(encodedPassword: String): Member =
            Member(name, nickname, email, encodedPassword)
    }

    data class Login(
        @field:NotBlank(message = "이메일은 필수 입력값입니다.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
        val password: String
    )

    data class Update(
        @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
        val nickname: String,

        @field:NotNull(message = "파일 ID는 필수 입력값입니다.")
        val fileId: Long
    )

    data class PasswordUpdate(
        @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
        val password: String
    )
}