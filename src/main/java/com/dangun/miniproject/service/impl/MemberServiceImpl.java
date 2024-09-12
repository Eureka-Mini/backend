package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Address;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetAddressRequest;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.AddressRepository;
import com.dangun.miniproject.repository.BoardRepository;
import com.dangun.miniproject.repository.CommentRepository;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

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

                    getMemberRequestBuilder.address(getAddressRequest);
                },
                () -> {
                    System.out.println("Address not found");
                }
        );

        return getMemberRequestBuilder.build();
    }

    @Override
    public ResponseEntity<GetMemberRequest> updateMember(GetMemberRequest getMemberRequest, Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateMember(getMemberRequest);

            memberRepository.save(member);

            GetMemberRequest updatedMemberRequest = GetMemberRequest.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .nickname(member.getNickname())
                    .address(getAddressRequest(member.getId()))
                    .build();

            return ResponseEntity.ok(updatedMemberRequest);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    private GetAddressRequest getAddressRequest(Long memberId) {
        // 주소 정보를 반환하는 메소드 (기본 예시)
        Optional<Address> addressOptional = addressRepository.findById(memberId);
        if (addressOptional.isPresent()) {
            Address address = addressOptional.get();
            return GetAddressRequest.builder()
                    .id(address.getId())
                    .street(address.getStreet())
                    .detail(address.getDetail())
                    .zipcode(address.getZipcode())
                    .build();
        }
        return null;
    }

    @Override
    public boolean deleteMember(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();

            // 연관된 Comment 삭제
            commentRepository.deleteByMemberId(id);

            // 연관된 Board 삭제
            boardRepository.deleteByMemberId(id);

            // 연관된 Address 삭제
            addressRepository.deleteByMemberId(id);

            // Member 삭제
            memberRepository.delete(member);
            return true;
        } else {
            return false;
        }
    }

}
