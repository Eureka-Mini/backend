package com.dangun.miniproject.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddressTest {
    private Address address;
    private Member member;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("hong@test.com")
                .nickname("Hong")
                .password("password123")
                .build();

        address = Address.builder()
                .street("123 주요 거리")
                .detail("101동 아파트")
                .zipcode("14352")
                .member(member) // Member 설정
                .build();
    }

    // 주소 객체 생성 테스트
    @Test
    public void createAddress() {
        assertNotNull(address);
        assertEquals("123 주요 거리", address.getStreet());
        assertEquals("101동 아파트", address.getDetail());
        assertEquals("14352", address.getZipcode());
        assertEquals(member, address.getMember()); // Member 설정 확인
    }

    // 주소와 멤버 관계 테스트
    @Test
    public void addressMemberRelationship() {
        assertNotNull(address.getMember());
        assertEquals("hong@test.com", address.getMember().getEmail());
        assertEquals("Hong", address.getMember().getNickname());
    }
}
