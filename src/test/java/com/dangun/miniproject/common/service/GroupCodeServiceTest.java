package com.dangun.miniproject.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dangun.miniproject.common.code.GroupCode;
import com.dangun.miniproject.common.dto.CodeResultDto;
import com.dangun.miniproject.common.repository.GroupCodeRepository;
import com.dangun.miniproject.fixture.GroupCodeFixture;

@ExtendWith(MockitoExtension.class)
class GroupCodeServiceTest {

	@InjectMocks
	private GroupCodeServiceImpl groupCodeService;

	@Mock
	private GroupCodeRepository groupCodeRepository;

	@Nested
	@DisplayName("GroupCode 생성")
	class InsertGroupCode {

		@Test
		@DisplayName("[성공] 요청한 값으로부터 GroupCode 가 성공적으로 생성된다.")
		void insert_groupCode_success() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode = GroupCodeFixture.instanceOf();

			given(groupCodeRepository.save(any())).willReturn(groupCode);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.insertGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("success");
		}

		@Test
		@DisplayName("[실패] 요청한 값으로부터 GroupCode 생성이 실패한다.")
		void insert_groupCode_fail() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode = new GroupCode();

			given(groupCodeRepository.save(any())).willThrow(RuntimeException.class);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.insertGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("fail");
		}
	}

	@Nested
	@DisplayName("GroupCode 수정")
	class UpdateGroupCode {

		@Test
		@DisplayName("[성공] 요청한 값으로 GroupCode 가 성공적으로 수정된다.")
		void update_groupCode_success() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode = GroupCodeFixture.instanceOf();

			given(groupCodeRepository.save(any())).willReturn(groupCode);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.updateGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("success");
		}

		@Test
		@DisplayName("[실패] 요청한 값으로 GroupCode 수정이 실패한다.")
		void update_groupCode_fail() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode = GroupCodeFixture.instanceOf();

			given(groupCodeRepository.save(any())).willThrow(RuntimeException.class);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.updateGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("fail");
		}
	}

	@Nested
	@DisplayName("GroupCode 삭제")
	class DeleteGroupCode {

		@Test
		@DisplayName("[성공] 해당 groupCode 가 성공적으로 삭제된다.")
		void delete_groupCode_success() {
			// given -- 테스트의 상태 설정
			final String groupCode = "010";

			willDoNothing().given(groupCodeRepository).deleteById(any());

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.deleteGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("success");
		}

		@Test
		@DisplayName("[실패] 해당 groupCode 삭제가 실패한다.")
		void delete_groupCode_fail() {
			// given -- 테스트의 상태 설정
			final String groupCode = "999";

			willThrow(RuntimeException.class).given(groupCodeRepository).deleteById(any());

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.deleteGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("fail");
		}
	}

	@Nested
	@DisplayName("GroupCode 전체 조회")
	class ListGroupCode {

		@Test
		@DisplayName("[성공] 전체 GroupCode 가 pageNumber, pageSize 에 맞게 페이지 처리된 후 조회된다.")
		void list_groupCode_paging_success() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode1 = GroupCodeFixture.instanceOf();
			final GroupCode groupCode2 = GroupCodeFixture.instanceOf();

			final PageRequest pageRequest = PageRequest.of(0, 10);
			final PageImpl<GroupCode> page = new PageImpl<>(List.of(groupCode1, groupCode2), pageRequest, 2);

			given(groupCodeRepository.findAll(any(Pageable.class))).willReturn(page);
			given(groupCodeRepository.count()).willReturn(2L);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.listGroupCode(0, 10);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result.getResult()).isEqualTo("success");
				softAssertions.assertThat(result.getCount()).isEqualTo(2L);
			});
		}

		@Test
		@DisplayName("[실패] 전체 GroupCode 가 조회가 실패한다.")
		void list_groupCode_paging_fail() {
			// given -- 테스트의 상태 설정
			given(groupCodeRepository.findAll(any(Pageable.class))).willThrow(RuntimeException.class);

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.listGroupCode(0, 10);

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("fail");
		}
	}

	@Nested
	@DisplayName("GroupCode 상세 조회")
	class DetailGroupCode {

		@Test
		@DisplayName("[성공] 해당 GroupCode 가 성공적으로 상세 조회된다.")
		void detail_groupCode_success() {
			// given -- 테스트의 상태 설정
			final GroupCode groupCode = GroupCodeFixture.instanceOf();

			given(groupCodeRepository.findById(any())).willReturn(Optional.of(groupCode));

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.detailGroupCode(groupCode.getGroupCode());

			// then -- 예상되는 변화 및 결과
			assertThat(result.getResult()).isEqualTo("success");
		}

		@Test
		@DisplayName("[실패] 해당 GroupCode 의 상세 조회가 실패한다.")
		void detail_groupCode_fail() {
			// given -- 테스트의 상태 설정
			final String groupCode = "999";

			given(groupCodeRepository.findById(any())).willReturn(Optional.empty());

			// when -- 테스트하고자 하는 행동
			final CodeResultDto result = groupCodeService.detailGroupCode(groupCode);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result.getResult()).isEqualTo("fail");
				softAssertions.assertThat(result.getGroupCodeDto()).isNull();
			});
		}
	}
}