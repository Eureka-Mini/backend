package com.dangun.miniproject.member.exception;

import com.dangun.miniproject.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("MEMBER-", "Member not found");
    }
}