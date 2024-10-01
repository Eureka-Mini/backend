package com.dangun.miniproject.common.service;

import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.dto.CodeDto;
import com.dangun.miniproject.common.dto.CodeResultDto;
import com.dangun.miniproject.common.repository.CodeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

    @Nested
    class listCode {

        @Test
        void 공통코드_리스트를_페이지로_조회한다() {
            // given
            String groupCode = BoardStatus.판매중.getGroupId();
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Code code = new Code(new CodeKey("010", "010"), "판매중", "sale", 1);
            Page<Code> codePage = new PageImpl<>(List.of(code));

            when(codeRepository.findByGroupCode(groupCode, pageable)).thenReturn(codePage);
            when(codeRepository.count()).thenReturn(1L);

            // when
            CodeResultDto result = codeService.listCode(groupCode, pageNumber, pageSize);

            // then
            Assertions.assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("success");
            assertThat(result.getCount()).isEqualTo(1);
            assertThat(result.getCodeDtoList().size()).isEqualTo(1);
            assertThat(result.getCodeDtoList().contains(CodeDto.fromCode(code))).isTrue();
            verify(codeRepository, times(1)).findByGroupCode(groupCode, pageable);
        }

        @Test
        void 공통코드_리스트_조회_실패() {
            // given
            String groupCode = BoardStatus.판매중.getGroupId();
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            when(codeRepository.findByGroupCode(groupCode, pageable)).thenThrow(new RuntimeException());

            // when
            CodeResultDto result = codeService.listCode(groupCode, pageNumber, pageSize);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("fail");
        }
    }

    @Nested
    class detailCode {

        @Test
        void 코드_상세_정보_조회() {
            // given
            CodeKey codeKey = new CodeKey("010", "010");
            Code code = new Code(codeKey, "판매중", "sale", 1);

            when(codeRepository.findById(codeKey)).thenReturn(Optional.of(code));

            // when
            CodeResultDto result = codeService.detailCode(codeKey);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("success");
            assertThat(result.getCodeDto()).isEqualTo(CodeDto.fromCode(code));
        }

        @Test
        void 유효하지_않은_codeKey_를_이용해_조회_시도() {
            // given
            CodeKey codeKey = new CodeKey("999", "010");

            when(codeRepository.findById(codeKey)).thenReturn(Optional.empty());

            // when
            CodeResultDto result = codeService.detailCode(codeKey);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getResult()).isEqualTo("fail");
            assertThat(result.getCodeDto()).isNull();
        }
    }
}
