package com.dangun.miniproject.member.domain;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.dto.GetMemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemberTest {
    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("hong@test.com")
                .nickname("Hong")
                .password("password123")
                .build();
    }

    // 회원 정보 업데이트 테스트
    @Test
    public void updateMember() {
        // Given
        GetMemberDto updateRequest = GetMemberDto.builder()
                .email("newemail@test.com")
                .nickname("NewHong")
                .build();

        // When
        member.updateMember(updateRequest);

        // Then
        assertEquals("newemail@test.com", member.getEmail());
        assertEquals("NewHong", member.getNickname());
    }

    // 주소 추가 테스트
    @Test
    public void addAddress() {
        // Given
        Address address = Address.builder()
                .street("123 주요 거리")
                .detail("101동 아파트")
                .zipcode("14352")
                .build();

        // When
        member.addAddress(address);

        // Then
        assertNotNull(member.getAddress());
        assertEquals(address.getStreet(), member.getAddress().getStreet());
        assertEquals(address.getDetail(), member.getAddress().getDetail());
        assertEquals(address.getZipcode(), member.getAddress().getZipcode());
    }

    // 비밀번호 확인 테스트
    @Test
    public void checkPassword() {
        // Given
        String inputPassword = "password123"; // 올바른 비밀번호

        // Then
        assertEquals(inputPassword, member.getPassword());
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
