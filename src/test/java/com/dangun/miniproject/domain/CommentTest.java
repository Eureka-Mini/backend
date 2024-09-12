package com.dangun.miniproject.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class CommentTest {

    @Test
    void Comment_객체를_생성한다() {
        // given
        Member member = Member.builder()
                .email("email")
                .build();
        Board board = Board.builder()
                .title("제목")
                .build();
        String content = "테스트 댓글";

        // when
        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .content(content)
                .build();

        // then
        assertSoftly(softly -> {
            softly.assertThat(comment.getId()).isNull();
            softly.assertThat(comment.getBoard()).isEqualTo(board);
            softly.assertThat(comment.getMember()).isEqualTo(member);
            softly.assertThat(comment.getContent()).isEqualTo(content);
        });
    }
}
