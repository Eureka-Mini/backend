package com.dangun.miniproject.common.code;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CodeKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupCode;
    private String code;
}
