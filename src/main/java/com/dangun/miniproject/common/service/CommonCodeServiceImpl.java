package com.dangun.miniproject.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.dto.CodeDto;
import com.dangun.miniproject.common.dto.CommonCodeResultDto;
import com.dangun.miniproject.common.repository.CommonCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService{

	private final CommonCodeRepository commonCodeRepository;

	@Override
	public CommonCodeResultDto getCommonCodeList(List<String> groupCodes) {
		CommonCodeResultDto commonCodeResultDto = new CommonCodeResultDto();

		try {
			List<Code> codeList = commonCodeRepository.findByGroupCodes(groupCodes);
			Map<String, List<CodeDto>> commonCodeListMap = new HashMap<>();
			String currGroupCode = "";
			List<CodeDto> codeDtoList = null;

			for (Code code : codeList) {
				String groupCode = code.getCodeKey().getGroupCode();

				if(! currGroupCode.equals(groupCode)) { // 두 그룹코드가 다르면

					if( Strings.isNotEmpty(currGroupCode) ) { // 최초가 아닌 currGroupCode 가 유효한 상황에서 변경되었다면
						commonCodeListMap.put(currGroupCode, codeDtoList);
					}
					currGroupCode = groupCode;
					codeDtoList = new ArrayList<>();
				}

				codeDtoList.add(CodeDto.fromCode(code));
			}

			commonCodeListMap.put(currGroupCode, codeDtoList); // 마지막 currGroupCode

			commonCodeResultDto.setCommonCodeDtoListMap(commonCodeListMap);
			commonCodeResultDto.setResult("success");

		} catch(Exception e) {
			e.printStackTrace();
			commonCodeResultDto.setResult("fail");
		}

		return commonCodeResultDto;
	}
}