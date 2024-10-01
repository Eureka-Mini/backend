package com.dangun.miniproject.common.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CommonCodeResultDto {
	private String result;
	private Map<String, List<CodeDto>> commonCodeDtoListMap;
}
