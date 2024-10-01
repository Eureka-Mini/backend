package com.dangun.miniproject.common.service;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.dto.CodeResultDto;
import com.dangun.miniproject.common.repository.CodeRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
            verify(codeRepository, times(1)).save(code);
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
            verify(codeRepository, times(1)).save(code);
        }
    }

    @Nested
    class updateCode {

        @Test
        void 공통_코드_수정_성공() {
            // given
            Code code = new Code();

            when(codeRepository.save(code)).thenReturn(code);

            // when
            CodeResultDto result = codeService.updateCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("success");
            verify(codeRepository, times(1)).save(code);
        }

        @Test
        void 공통_코드_수정_실패() {
            // given
            Code code = new Code();

            when(codeRepository.save(code)).thenThrow(new RuntimeException());

            // when
            CodeResultDto result = codeService.updateCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("fail");
            verify(codeRepository, times(1)).save(code);
        }
    }

    @Nested
    class deleteCode {

        @Test
        void 공통_코드_삭제_성공() {
            // given
            CodeKey code = new CodeKey();

            doNothing().when(codeRepository).deleteById(code);

            // when
            CodeResultDto result = codeService.deleteCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("success");
        }

        @Test
        void 공통_코드_삭제_실패() {
            // given
            CodeKey code = new CodeKey();

            doThrow(new RuntimeException()).when(codeRepository).deleteById(code);

            // when
            CodeResultDto result = codeService.deleteCode(code);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("fail");
        }
    }
}
