package com.dangun.miniproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Builder
    private Address(String street, String zipcode, String detail) {
        this.street = street;
        this.zipcode = zipcode;
        this.detail = detail;
    }
}
