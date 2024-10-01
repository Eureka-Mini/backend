package com.dangun.miniproject.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MemberGradeTest {

    @Test
    @DisplayName("[성공] MemberGrade의 각 값이 올바르게 설정된다.")
    void memberGrade_values_success() {
        // when & then -- 각 열거형 값의 코드 ID와 그룹 ID를 확인
        assertThat(MemberGrade.실버.getCodeId()).isEqualTo("010");
        assertThat(MemberGrade.실버.getGroupId()).isEqualTo("020");

        assertThat(MemberGrade.골드.getCodeId()).isEqualTo("020");
        assertThat(MemberGrade.골드.getGroupId()).isEqualTo("020");

        assertThat(MemberGrade.VIP.getCodeId()).isEqualTo("030");
        assertThat(MemberGrade.VIP.getGroupId()).isEqualTo("020");
    }
}

