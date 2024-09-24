package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.auth.service.impl.UserDetailsServiceImpl;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 유효한_이메일로_유저를_찾는다(){
        // given
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(anyString())).thenReturn(member);

        // when
        userDetailsService.loadUserByUsername(anyString());

        // then
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void 유저를_찾지_못한다(){
        // given
        when(memberRepository.findByEmail(anyString())).thenReturn(null);

        // when & then
        Assertions.assertThatThrownBy(()->userDetailsService.loadUserByUsername(anyString()))
                .isInstanceOf(UsernameNotFoundException.class).hasMessage("해당 유저를 찾을 수 없습니다.");
    }
}
