package com.dangun.miniproject.common.dto;

import com.dangun.miniproject.common.code.GroupCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GroupCodeDto {
	private String groupCode;
	private String groupCodeName;
	private String groupCodeDesc;

	public static GroupCodeDto fromGroupCode(GroupCode groupCode) {
		return GroupCodeDto.builder()
			.groupCode(groupCode.getGroupCode())
			.groupCodeName(groupCode.getGroupCodeName())
			.groupCodeDesc(groupCode.getGroupCodeDesc())
			.build();
	}
}
