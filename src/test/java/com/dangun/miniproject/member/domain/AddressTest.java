package com.dangun.miniproject.member.domain;

import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.member.dto.GetAddressDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class AddressTest {

    @Test
    public void updateAddress() {
        // Given
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("testNickname")
                .password("password123")
                .build();

        Address address = Address.builder()
                .member(member)
                .street("Street")
                .detail("Detail")
                .zipcode("11111")
                .build();

        GetAddressDto updateDto = GetAddressDto.builder()
                .street("newStreet")
                .detail("newDetail")
                .zipcode("00000")
                .build();

        // When
        address.updateAddress(updateDto);

        // Then
        setField(address, "id", 1L);
        assertThat(address.getId()).isEqualTo(address.getId());
        assertThat(address.getStreet()).isEqualTo("newStreet");
        assertThat(address.getDetail()).isEqualTo("newDetail");
        assertThat(address.getZipcode()).isEqualTo("00000");
    }
}