package com.dangun.miniproject.member.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dangun.miniproject.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MemberRepositoryTest {

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("hong@test.com")
                .nickname("Hong")
                .password("password123")
                .build();
    }

    // 이메일 존재 여부 확인 테스트
    @Test
    public void existsByEmail() {
        // Given
        when(memberRepository.existsByEmail("hong@test.com")).thenReturn(true);

        // When
        Boolean result = memberRepository.existsByEmail("hong@test.com");

        // Then
        assertTrue(result);
        verify(memberRepository).existsByEmail("hong@test.com");
    }

    // 이메일로 회원 조회 테스트
    @Test
    public void findByEmail() {
        // Given
        when(memberRepository.findByEmail("hong@test.com")).thenReturn(member);

        // When
        Member result = memberRepository.findByEmail("hong@test.com");

        // Then
        assertNotNull(result);
        assertEquals("hong@test.com", result.getEmail());
        assertEquals("Hong", result.getNickname());
        verify(memberRepository).findByEmail("hong@test.com");
    }

    // 닉네임 존재 여부 확인 테스트
    @Test
    public void existsByNickname() {
        // Given
        when(memberRepository.existsByNickname("Hong")).thenReturn(true);

        // When
        Boolean result = memberRepository.existsByNickname("Hong");

        // Then
        assertTrue(result);
        verify(memberRepository).existsByNickname("Hong");
    }
}
