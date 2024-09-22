package com.dangun.miniproject.member.domain;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.dto.GetMemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemberTest {

    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .nickname("tester")
                .password("password1234")
                .build();
    }

    @Test
    public void testMemberCreationWithBuilder() {
        // Given
        String email = "test@example.com";
        String nickname = "tester";
        String password = "password1234";

        // When
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();

        // Then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getPassword()).isEqualTo(password);
    }


    // 회원 정보 업데이트 테스트
    @Test
    public void updateMember() {
        // Given
        GetMemberDto updateDto = GetMemberDto.builder()
                .nickname("newNickname")
                .build();

        // When
        member.updateMember(updateDto);

        // Then
        assertThat(member.getNickname()).isEqualTo("newNickname"); // nickname은 변경되지 않아야 함
    }


    // 주소 추가 테스트
    @Test
    public void addAddress() {
        // Given
        Address address = Address.builder()
                .street("street")
                .detail("detail")
                .zipcode("11111")
                .build();

        // When
        member.addAddress(address);

        // Then
        assertNotNull(member.getAddress());
        assertEquals(address.getStreet(), member.getAddress().getStreet());
        assertEquals(address.getDetail(), member.getAddress().getDetail());
        assertEquals(address.getZipcode(), member.getAddress().getZipcode());
    }

    // 댓글 삭제 테스트
    @Test
    public void removeComment() {
        // Given
        Board board = new Board(); // Board 객체 생성 (테스트를 위한 빈 객체)
        Comment comment = Comment.builder()
                .content("이것은 댓글입니다.")
                .member(member) // 댓글과 멤버의 관계 설정
                .board(board) // 댓글과 게시판의 관계 설정
                .build();
        member.getComments().add(comment); // 댓글 추가

        // When
        member.getComments().remove(comment);

        // Then
        assertEquals(0, member.getComments().size());
    }

}
