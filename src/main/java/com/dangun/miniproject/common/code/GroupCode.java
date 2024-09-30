package com.dangun.miniproject.common.code;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class GroupCode {
    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "group_code_name")
    private String groupCodeName;

    @Column(name = "group_code_desc")
    private String groupCodeDesc;
}
