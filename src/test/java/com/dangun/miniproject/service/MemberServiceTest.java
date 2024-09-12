package com.dangun.miniproject.service;

import com.dangun.miniproject.domain.Address;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetAddressRequest;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.AddressRepository;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private Address address;


    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("Hong@test.com")
                .nickname("Hong")
                .password("1234")
                .build();

        address = Address.builder()
                .street("123 주요 거리")
                .detail("101동 아파트")
                .zipcode("14352")
                .member(member)
                .build();
    }


    // 회원 조회 테스트
    @Test
    public void getMember() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        // When
        GetMemberRequest result = memberService.getMember(1L);

        // Then
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());
        assertEquals(member.getPassword(), result.getPassword());

        assertNotNull(result.getAddress());
        assertEquals(address.getId(), result.getAddress().getId());
        assertEquals(address.getStreet(), result.getAddress().getStreet());
        assertEquals(address.getDetail(), result.getAddress().getDetail());
        assertEquals(address.getZipcode(), result.getAddress().getZipcode());
    }

    // 회원 수정 테스트
    @Test
    void updateMember() {
        // given
        Long memberId = 1L;
        GetMemberRequest updateRequest = new GetMemberRequest(
                memberId,
                "Hong@test.com",
                "1234",
                "Hong",
                null // 주소는 업데이트하지 않으므로 null
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        ResponseEntity<GetMemberRequest> response = memberService.updateMember(updateRequest, memberId);

        // then
        Optional<GetMemberRequest> optionalBody = Optional.ofNullable(response.getBody());
        optionalBody.ifPresent(body -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Hong@test.com", body.getEmail());
            assertEquals("1234", response.getBody().getPassword());
            assertEquals("Hong", response.getBody().getNickname());
            // 주소에 대한 검증은 생략
        });

    }
}
