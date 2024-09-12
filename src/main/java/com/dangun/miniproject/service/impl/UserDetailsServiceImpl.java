package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UserDetailsDto;
import com.dangun.miniproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
        }

        return new UserDetailsDto(member);
    }
}
