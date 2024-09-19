package com.dangun.miniproject.member.service;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressDto;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
                .email("test@example.com")
                .nickname("tester")
                .build();

        address = Address.builder()
                .street("street")
                .detail("detail")
                .zipcode("11111")
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

    @Test
    public void getMember_NotFound() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When
        GetMemberDto result = memberService.getMember(memberId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getNickname()).isNull();
        assertThat(result.getAddress()).isNull();
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


    // 회원 수정 테스트
    @Test
    public void updateMember() {
        Long memberId = 1L;
        GetMemberDto updateDto = GetMemberDto.builder()
                .email("newTest@example.com")
                .nickname("newNickname")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        GetMemberDto updatedMemberDto = memberService.updateMember(updateDto, memberId);

        // Then
        assertThat(updatedMemberDto).isNotNull();
        assertThat(updatedMemberDto.getEmail()).isEqualTo("newTest@example.com");
        assertThat(updatedMemberDto.getNickname()).isEqualTo("newNickname");
        assertThat(updatedMemberDto.getAddress()).isNotNull();
        assertThat(updatedMemberDto.getAddress().getStreet()).isEqualTo("street");
        assertThat(updatedMemberDto.getAddress().getDetail()).isEqualTo("detail");
        assertThat(updatedMemberDto.getAddress().getZipcode()).isEqualTo("11111");

        verify(memberRepository).save(member);
    }


    // 주소 정보 조회 테스트
    @Test
    public void updateAddress() {
        Long addressId = 1L;
        GetAddressDto updateDto = GetAddressDto.builder()
                .street("newStreet")
                .detail("newDetail")
                .zipcode("00000")
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // When
        GetAddressDto updatedAddressDto = memberService.updateAddress(updateDto, addressId);

        // Then
        assertThat(updatedAddressDto).isNotNull();
        assertThat(updatedAddressDto.getStreet()).isEqualTo("newStreet");
        assertThat(updatedAddressDto.getDetail()).isEqualTo("newDetail");
        assertThat(updatedAddressDto.getZipcode()).isEqualTo("00000");

        verify(addressRepository).save(address);
    }


    @Test
    void deleteMember() {
        // given
        setField(member, "comments", new ArrayList<Comment>());
        setField(member, "boards", new ArrayList<Board>());
        setField(member, "address", new Address());

        // 댓글 및 게시글 추가
        Comment comment = mock(Comment.class);
        Board board = mock(Board.class);

        List<Comment> comments = (List<Comment>) getField(member, "comments");
        comments.add(comment);

        List<Board> boards = (List<Board>) getField(member, "boards");
        boards.add(board);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        boolean result = memberService.deleteMember(1L);

        // then
        assertTrue(result);
        verify(commentRepository).deleteInBatch(member.getComments());
        verify(boardRepository).deleteInBatch(member.getBoards());
        verify(addressRepository).delete(member.getAddress());
        verify(memberRepository).delete(member);
    }
}
