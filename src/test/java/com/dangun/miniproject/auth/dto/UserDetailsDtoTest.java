package com.dangun.miniproject.auth.dto;

import com.dangun.miniproject.fixture.AddressFixture;
import com.dangun.miniproject.fixture.MemberFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsDtoTest {

    @Test
    @DisplayName("UserDetails Dto 생성 성공")
    void testUserDetailsMethods() {
        GetMemberRequest memberRequest = MemberFixture.instanceOf();
        Member member = memberRequest.toEntity();

        Address address = AddressFixture.instanceOf(member);
        member.addAddress(address);

        UserDetailsDto userDetails = new UserDetailsDto(member);

        String email = userDetails.getUsername();
        String nickname = userDetails.getNickname();
        String password = userDetails.getPassword();
        Member memberDetail = userDetails.getMember();

        assertEquals(email, userDetails.getUsername());
        assertEquals(nickname, userDetails.getNickname());
        assertEquals(password, userDetails.getPassword());
        assertEquals(memberDetail, userDetails.getMember());

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("UserDetails Authorities 생성 성공")
    void testAuthorities() {
        GetMemberRequest memberRequest = MemberFixture.instanceOf();
        Member member = memberRequest.toEntity();

        Address address = AddressFixture.instanceOf(member);
        member.addAddress(address);

        UserDetailsDto userDetails = new UserDetailsDto(member);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
