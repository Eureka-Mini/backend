package com.dangun.miniproject.common.code;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Code {

    @EmbeddedId
    CodeKey codeKey;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "code_name_brief")
    private String codeNameBrief;

    @Column(name = "order_no")
    private int orderNo; // order 로 하면 예약어로 오류 발생
}
