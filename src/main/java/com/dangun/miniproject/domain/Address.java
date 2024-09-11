package com.dangun.miniproject.domain;

import static jakarta.persistence.FetchType.*;

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
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Address(String detail, Member member, String street, String zipcode) {
        this.detail = detail;
        this.member = member;
        this.street = street;
        this.zipcode = zipcode;
    }
}
