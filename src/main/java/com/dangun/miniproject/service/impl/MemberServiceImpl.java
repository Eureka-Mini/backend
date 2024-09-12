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
    public GetMemberRequest updateMember(GetMemberRequest getMemberRequest,Long id) {

        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isPresent()) {

            Member member = optionalMember.get();

            // 수정할 필드만 업데이트 (주소는 수정 X)
            member.updateMember(getMemberRequest);

            try {
                memberRepository.save(member);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            return null;
        }

        return getMemberRequest;
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
