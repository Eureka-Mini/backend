package com.dangun.miniproject.board.domain;

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

    public static BoardStatus fromCodeId(String codeId) {
        for (BoardStatus status : BoardStatus.values()) {
            if (status.getCodeId().equals(codeId)) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 codeId : " + codeId);
    }
}