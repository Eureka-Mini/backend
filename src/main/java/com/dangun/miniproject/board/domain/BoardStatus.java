package com.dangun.miniproject.board.domain;

import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.exception.InvalidInputException;
import lombok.Getter;

@Getter
public enum BoardStatus {
    판매중("010", "010"), 판매완료("020", "010");

    private String codeId;
    private String groupId;

    BoardStatus(String codeId, String groupId) {
        this.codeId = codeId;
        this.groupId = groupId;
    }

    public static BoardStatus from(CodeKey codeKey) {
        for (BoardStatus status : BoardStatus.values()) {
            if (status.getCodeId().equals(codeKey.getCode()) && status.getGroupId().equals(codeKey.getGroupCode())) {
                return status;
            }
        }
        throw new InvalidInputException("유효하지 않은 codeKey : " + codeKey);
    }
}