package com.memory.service.member;

import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.response.MemberLoginResponse;
import com.memory.dto.member.response.MemberResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberService memberService;

    private MemberRequest.Signup signupRequest;
    private MemberRequest.Login loginRequest;
    private Member member;
    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String password = "password";
    private final String encodedPassword = "encodedPassword";
    private final String name = "Test User";
    private final String nickname = "testuser";
    @BeforeEach
    void setUp() {
        signupRequest = new MemberRequest.Signup(email, password, name, nickname);
        loginRequest = new MemberRequest.Login(email, password);

        member = new Member(name, nickname, email, encodedPassword);
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, memberId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set member ID", e);
        }
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess() {
        // Given
        when(memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        MemberResponse response = memberService.signup(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals(memberId, response.id());
        assertEquals(email, response.email());
        assertEquals(name, response.name());
        assertEquals(nickname, response.nickname());
        assertEquals(MemberType.MEMBER, response.memberType());
        assertNull(response.profile());  // No file associated with the member yet

        verify(memberRepository).findMemberByEmailAndMemberType(email, MemberType.MEMBER);
        verify(passwordEncoder).encode(password);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이미 존재하는 이메일")
    void signupFailDuplicateEmail() {
        // Given
        when(memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)).thenReturn(Optional.of(member));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> memberService.signup(signupRequest));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(memberRepository).findMemberByEmailAndMemberType(email, MemberType.MEMBER);
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // Given
        when(memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        String accessToken = "access.token.example";
        when(jwtTokenProvider.createAccessToken(email)).thenReturn(accessToken);
        String refreshToken = "refresh.token.example";
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn(refreshToken);

        // When
        MemberLoginResponse response = memberService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(memberRepository).findMemberByEmailAndMemberType(email, MemberType.MEMBER);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtTokenProvider).createAccessToken(email);
        verify(jwtTokenProvider).createRefreshToken(email);
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 이메일")
    void loginFailEmailNotFound() {
        // Given
        when(memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> memberService.login(loginRequest));

        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
        verify(memberRepository).findMemberByEmailAndMemberType(email, MemberType.MEMBER);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).createAccessToken(anyString());
        verify(jwtTokenProvider, never()).createRefreshToken(anyString());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void loginFailPasswordMismatch() {
        // Given
        when(memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> memberService.login(loginRequest));

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(memberRepository).findMemberByEmailAndMemberType(email, MemberType.MEMBER);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtTokenProvider, never()).createAccessToken(anyString());
        verify(jwtTokenProvider, never()).createRefreshToken(anyString());
    }

    @Test
    @DisplayName("회원 조회 성공 테스트")
    void findMemberByIdSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));

        // When
        MemberResponse response = memberService.findMemberById(memberId);

        // Then
        assertNotNull(response);
        assertEquals(memberId, response.id());
        assertEquals(email, response.email());
        assertEquals(name, response.name());
        assertEquals(nickname, response.nickname());
        assertEquals(MemberType.MEMBER, response.memberType());
        assertNull(response.profile());  // No file associated with the member yet

        verify(memberRepository).findMemberById(memberId);
    }

    @Test
    @DisplayName("회원 조회 실패 테스트 - 존재하지 않는 회원")
    void findMemberByIdFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> memberService.findMemberById(memberId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
    }

    @Test
    @DisplayName("회원 타입 테스트 - ADMIN 타입 설정")
    void memberTypeAdminTest() {
        // Given
        Member adminMember = new Member(name, nickname, email, encodedPassword, MemberType.ADMIN);
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(adminMember, memberId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set member ID", e);
        }

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(adminMember));

        // When
        MemberResponse response = memberService.findMemberById(memberId);

        // Then
        assertNotNull(response);
        assertEquals(MemberType.ADMIN, response.memberType());
        assertNull(response.profile());  // No file associated with the member yet
        verify(memberRepository).findMemberById(memberId);
    }
}
