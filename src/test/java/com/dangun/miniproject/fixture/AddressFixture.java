package com.dangun.miniproject.fixture;

import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;

import static java.util.concurrent.ThreadLocalRandom.current;

public class AddressFixture {

    public static Address instanceOf(final Member member) {

        final String street = "street" + current().nextInt(100, 1000);
        final String detail = "detail" + current().nextInt(100, 1000);
        final String zipcode = String.valueOf(current().nextInt(10000, 99999));

        return Address.builder()
                .street(street)
                .detail(detail)
                .zipcode(zipcode)
                .member(member)
                .build();
    }
}
