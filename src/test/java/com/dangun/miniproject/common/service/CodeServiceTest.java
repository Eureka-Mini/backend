package com.dangun.miniproject.common.service;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.dto.CodeResultDto;
import com.dangun.miniproject.common.repository.CodeRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CodeServiceTest {

    @InjectMocks
    private CodeServiceImpl codeService;

    @Mock
    private CodeRepository codeRepository;

    @Nested
    class insertCode {

        @Test
        void 공통코드_생성_성공() {
            // given
            Code code = new Code();

            when(codeRepository.save(code)).thenReturn(code);

            // when
            CodeResultDto result = codeService.insertCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("success");
        }

        @Test
        void 공통코드_생성_실패() {
            // given
            Code code = new Code();

            when(codeRepository.save(code)).thenThrow(new RuntimeException());

            // when
            CodeResultDto result = codeService.insertCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("fail");
        }
    }
}
