package com.dangun.miniproject.common.service;


import com.dangun.miniproject.common.code.GroupCode;
import com.dangun.miniproject.common.dto.CodeResultDto;

public interface GroupCodeService {
	CodeResultDto insertGroupCode(GroupCode groupCode);
	CodeResultDto updateGroupCode(GroupCode groupCode);
	CodeResultDto deleteGroupCode(String groupCode);	
	
	CodeResultDto listGroupCode(int pageNumber, int pageSize);
	CodeResultDto countGroupCode();
	CodeResultDto detailGroupCode(String groupCode);
}
