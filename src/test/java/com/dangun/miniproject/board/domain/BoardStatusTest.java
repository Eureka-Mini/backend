package com.dangun.miniproject.board.domain;

import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.exception.InvalidInputException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardStatusTest {

    @Test
    void 올바른_CodeKey_를_이용하여_판매중_상태를_찾는다(){
        // given
        CodeKey codeKey = new CodeKey("010", "010");

        // when
        BoardStatus boardStatus = BoardStatus.from(codeKey);

        // then
        Assertions.assertThat(boardStatus).isNotNull();
        Assertions.assertThat(boardStatus).isEqualTo(BoardStatus.판매중);
        Assertions.assertThat(boardStatus.getCodeId()).isEqualTo(codeKey.getCode());
        Assertions.assertThat(boardStatus.getGroupId()).isEqualTo(codeKey.getGroupCode());
    }

    @Test
    void 올바르지_않은_CodeKey_를_이용하여_에러_발생(){
        // given
        CodeKey codeKey = new CodeKey("999", "999");

        // when & then
        Assertions.assertThatThrownBy(() -> BoardStatus.from(codeKey))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("유효하지 않은 codeKey : " + codeKey);
    }

    @Test
    void 존재하지_않는_GroupCode_를_가진_CodeKey_를_이용하여_에러_발생(){
        // given
        CodeKey codeKey = new CodeKey("999", "010");

        // when & then
        Assertions.assertThatThrownBy(() -> BoardStatus.from(codeKey))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("유효하지 않은 codeKey : " + codeKey);
    }
}
