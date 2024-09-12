package com.dangun.miniproject.repository;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager em;

    private Board board;
    private Member member;

    @BeforeEach
    void init() {
        member = em.find(Member.class, 6L);
        board = em.find(Board.class, 1L);
    }

    @Test
    void 유효한_유저와_게시판을_이용해_Comment_생성() {
        // given
        String commentContent = "테스트 댓글";

        Comment comment = Comment.builder()
                .member(member)
                .board(board)
                .content(commentContent)
                .build();

        // when
        comment = commentRepository.save(comment);

        // then
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getMember().getId()).isEqualTo(6L);
        assertThat(comment.getBoard().getId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo(commentContent);
    }
}
