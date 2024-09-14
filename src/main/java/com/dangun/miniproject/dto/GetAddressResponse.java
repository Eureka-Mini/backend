package com.dangun.miniproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class GetAddressResponse {
    private Long id;
    private String street;
    private String detail;
    private String zipcode;

    @Builder
    public GetAddressResponse(Long id, String street, String detail, String zipcode) {
        this.id = id;
        this.street = street;
        this.detail = detail;
        this.zipcode = zipcode;
    }
}
