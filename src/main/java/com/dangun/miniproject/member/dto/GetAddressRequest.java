package com.dangun.miniproject.member.dto;

import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAddressRequest {
    private String street;
    private String detail;
    private String zipcode;

    public Address toEntity(Member member) {
        return Address.builder()
                .zipcode(zipcode)
                .detail(detail)
                .street(street)
                .build();
    }
}
