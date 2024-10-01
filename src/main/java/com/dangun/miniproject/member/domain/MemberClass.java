package com.dangun.miniproject.member.domain;

import lombok.Getter;

@Getter
public enum MemberClass {
    실버("010", "020"), 골드("020", "020"), VIP("030","020");

    private String codeId;
    private String groupId;

    MemberClass(String codeId, String groupId) {
        this.codeId = codeId;
        this.groupId = groupId;
    }
}
