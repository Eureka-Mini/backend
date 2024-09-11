package com.dangun.miniproject.service;

import com.dangun.miniproject.domain.Address;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetAddressRequest;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.AddressRepository;
import com.dangun.miniproject.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;

    @Override
    public GetMemberRequest getMember(Long id) {
        // Member 엔티티를 조회
        Optional<Member> optionalMember = memberRepository.findById(id);

        // MemberDto를 초기화
        GetMemberRequest.GetMemberRequestBuilder getMemberRequestBuilder = GetMemberRequest.builder();

        optionalMember.ifPresentOrElse(
                member -> {
                    // Member 엔티티 정보를 기반으로 GetMemberRequest(Dto)를 빌드
                    getMemberRequestBuilder
                            .id(member.getId())
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .password(member.getPassword());
                },
                () -> {
                    System.out.println("Member not found");
                }
        );

        // Address 정보를 조회
        Optional<Address> optionalAddress = addressRepository.findById(id);

        optionalAddress.ifPresentOrElse(
                address -> {
                    // Address 엔티티 정보를 getAddressRequest(Dto)로 변환
                    GetAddressRequest getAddressRequest = GetAddressRequest.builder()
                            .id(address.getId())
                            .street(address.getStreet())
                            .detail(address.getDetail())
                            .zipcode(address.getZipcode())
                            .build();

                    // MemberDto에 AddressDto를 설정
                    getMemberRequestBuilder.address(getAddressRequest);
                },
                () -> {
                    System.out.println("Address not found");
                }
        );

        // 최종적으로 MemberDto를 빌드하고 반환
        return getMemberRequestBuilder.build();
    }
}
