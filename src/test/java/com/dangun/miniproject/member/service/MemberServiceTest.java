package com.dangun.miniproject.member.service;

import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.repository.AddressRepository;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.comment.repository.CommentRepository;
import com.dangun.miniproject.member.repository.MemberRepository;
import com.dangun.miniproject.member.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private MemberServiceImpl memberService;


    private Member member;
    private Address address;


    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("Hong@test.com")
                .nickname("Hong")
                .build();

        address = Address.builder()
                .street("123 주요 거리")
                .detail("101동 아파트")
                .zipcode("14352")
                .member(member)
                .build();

        member.addAddress(address); // Member에 주소 추가
    }


    // 회원 조회 테스트
    @Test
    public void getMember() {
        // Given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(addressRepository.findById(member.getId())).thenReturn(Optional.of(address));

        // When
        GetMemberDto result = memberService.getMember(member.getId());

        // Then
        assertNotNull(result);
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());

        assertNotNull(result.getAddress());
        assertEquals(address.getStreet(), result.getAddress().getStreet());
        assertEquals(address.getDetail(), result.getAddress().getDetail());
        assertEquals(address.getZipcode(), result.getAddress().getZipcode());
    }


    // 본인 정보 조회 테스트
    @Test
    public void getMyInfo() {
        // Given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        // When
        GetMemberDto result = memberService.getMyInfo(member.getId());

        // Then
        assertNotNull(result);
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());
        assertNotNull(result.getAddress());
        assertEquals(member.getAddress().getStreet(), result.getAddress().getStreet());
        assertEquals(member.getAddress().getDetail(), result.getAddress().getDetail());
        assertEquals(member.getAddress().getZipcode(), result.getAddress().getZipcode());
    }


//    // 회원 수정 테스트
//    @Test
//    void updateMember() {
//        // given
//        Long memberId = 1L;
//        GetMemberDto updateRequest = new GetMemberDto(
//                "Hong@test.com",
//                "Hong",
//                null // 주소는 업데이트하지 않으므로 null
//        );
//
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//        when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//        // when
//        ResponseEntity<GetMemberDto> response = memberService.updateMember(updateRequest, memberId);
//
//        // then
//        Optional<GetMemberDto> optionalBody = Optional.ofNullable(response.getBody());
//        optionalBody.ifPresent(body -> {
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals("Hong@test.com", body.getEmail());
//            assertEquals("Hong", response.getBody().getNickname());
//        });
//    }

    // 주소 정보 조회 테스트
    @Test
    public void getAddressDtoWhenAddressExists() {
        // given
        Long memberId = 1L;
        when(addressRepository.findById(memberId)).thenReturn(Optional.of(address));

        // When
        Optional<Address> result = addressRepository.findById(memberId);

        // Then
        assertNotNull(result);
        assertEquals(address.getStreet(), result.get().getStreet());
        assertEquals(address.getDetail(), result.get().getDetail());
        assertEquals(address.getZipcode(), result.get().getZipcode());
    }


    @Test
    void deleteMember() {
        // given
        Long memberId = 1L;
        Member member = new Member(); // 필요한 멤버 객체 생성
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        boolean result = memberService.deleteMember(memberId);

        // then
        assertTrue(result);  // 멤버가 존재하면 true

        // Verify that deletions happened
        verify(commentRepository, times(1)).deleteByMemberId(memberId);
        verify(boardRepository, times(1)).deleteByMemberId(memberId);
        verify(addressRepository, times(1)).deleteByMemberId(memberId);
        verify(memberRepository, times(1)).delete(member);    }

}
