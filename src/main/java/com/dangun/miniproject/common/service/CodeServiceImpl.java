package com.dangun.miniproject.common.service;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.dto.CodeDto;
import com.dangun.miniproject.common.dto.CodeResultDto;
import com.dangun.miniproject.common.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService{

	private final CodeRepository codeRepository;
	@Override
	public CodeResultDto insertCode(Code code) {
		CodeResultDto codeResultDto = new CodeResultDto();
		try {
			codeRepository.save(code);
			codeResultDto.setResult("success");
		}catch(Exception e) {
			e.printStackTrace();
			codeResultDto.setResult("fail");
		}
		return codeResultDto;
	}

	@Override
	public CodeResultDto updateCode(Code code) {
		CodeResultDto codeResultDto = new CodeResultDto();
		try {
			// select 후 update
			codeRepository.save(code);
			codeResultDto.setResult("success");
		}catch(Exception e) {
			e.printStackTrace();
			codeResultDto.setResult("fail");
		}
		return codeResultDto;
	}

	@Override
	public CodeResultDto deleteCode(CodeKey codeKey) {
		CodeResultDto codeResultDto = new CodeResultDto();
		try {
			codeRepository.deleteById(codeKey);
			codeResultDto.setResult("success");
		}catch(Exception e) {
			e.printStackTrace();
			codeResultDto.setResult("fail");
		}
		return codeResultDto;		
	}

	@Override
	public CodeResultDto listCode(String groupCode, int pageNumber, int pageSize) {
		CodeResultDto codeResultDto = new CodeResultDto();
		try {
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			Page<Code> page = codeRepository.findByGroupCode(groupCode, pageable);
			List<CodeDto> codeDtoList = new ArrayList<>();
			page.toList().forEach( code -> codeDtoList.add(CodeDto.fromCode(code)) );
			codeResultDto.setCodeDtoList(codeDtoList);
			
			Long count = codeRepository.count(); // int -> Long
			codeResultDto.setCount(count);
			
			codeResultDto.setResult("success");
		}catch(Exception e) {
			e.printStackTrace();
			codeResultDto.setResult("fail");
		}
		return codeResultDto;
	}

	@Override
	public CodeResultDto countCode() {
		CodeResultDto codeResultDto = new CodeResultDto();
		long count = codeRepository.count();
		codeResultDto.setCount(count);
		return codeResultDto;
	}

	@Override
	public CodeResultDto detailCode(CodeKey codeKey) {
		CodeResultDto codeResultDto = new CodeResultDto();
		Optional<Code> optionalCode = codeRepository.findById(codeKey);
		optionalCode.ifPresentOrElse(
				code -> {
					codeResultDto.setCodeDto( CodeDto.fromCode(code));
					codeResultDto.setResult("success");
				},
				() -> {
					codeResultDto.setResult("fail");
				});
		return codeResultDto;
	}

}
