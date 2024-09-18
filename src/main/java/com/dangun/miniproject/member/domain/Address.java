package com.dangun.miniproject.member.domain;

import static jakarta.persistence.FetchType.*;

import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Address {
    @Id
    private Long id;
    private String street;
    private String detail;
    private String zipcode;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Address(String detail, Member member, String street, String zipcode) {
        this.detail = detail;
        this.member = member;
        this.street = street;
        this.zipcode = zipcode;
    }


    public void updateAddress(GetAddressDto getAddressDto){
        if (getAddressDto.getStreet() != null) {
            this.street = getAddressDto.getStreet();
        }
        if (getAddressDto.getDetail() != null) {
            this.detail = getAddressDto.getDetail();
        }
        if (getAddressDto.getZipcode() != null) {
            this.zipcode = getAddressDto.getZipcode();
        }
    }
}
