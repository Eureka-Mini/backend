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
}