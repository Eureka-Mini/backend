package com.dangun.miniproject.repository;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressRepositoryTest {

    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        // 초기 설정이 필요하지 않음
    }

    // 특정 회원 ID로 주소 삭제 테스트
    @Test
    public void deleteByMemberId() {
        // Given
        Long memberId = 1L;

        // When
        addressRepository.deleteByMemberId(memberId);

        // Then
        verify(addressRepository).deleteByMemberId(memberId);
    }
}
