package com.dangun.miniproject.common.service;


import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.dto.CodeResultDto;

public interface CodeService {
	CodeResultDto insertCode(Code code);
	CodeResultDto updateCode(Code code);
	CodeResultDto deleteCode(CodeKey codeKey);
	
	CodeResultDto listCode(String groupCode, int pageNumber, int pageSize);
	CodeResultDto detailCode(CodeKey codeKey);	
}
