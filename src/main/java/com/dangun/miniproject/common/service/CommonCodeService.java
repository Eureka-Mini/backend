package com.dangun.miniproject.common.service;

import java.util.List;

import com.dangun.miniproject.common.dto.CommonCodeResultDto;

public interface CommonCodeService {
	CommonCodeResultDto getCommonCodeList(List<String> goupCodes);
}
