package com.dangun.miniproject.member.exception;

import com.dangun.miniproject.common.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {
    public AddressNotFoundException() {
        super("MEMBER-", "Address not found");
    }
}