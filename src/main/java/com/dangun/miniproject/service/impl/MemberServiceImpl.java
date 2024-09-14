package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Address;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetAddressResponse;
import com.dangun.miniproject.dto.GetAddressDto;
import com.dangun.miniproject.dto.GetMemberDto;
import com.dangun.miniproject.dto.GetMemberResponse;
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
    public GetMemberDto getMember(Long id) {
        // Member 엔티티를 조회
        Optional<Member> optionalMember = memberRepository.findById(id);

        // MemberDto를 초기화
        GetMemberDto.GetMemberDtoBuilder getMemberDtoBuilder = GetMemberDto.builder();

        optionalMember.ifPresentOrElse(
                member -> {
                    // Member 엔티티 정보를 기반으로 GetMemberRequest(Dto)를 빌드
                    getMemberDtoBuilder
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .address(getAddressDto(member.getId()));
                },
                () -> {
                    System.out.println("Member not found");
                }
        );

        return getMemberDtoBuilder.build();
    }

    @Override
    public GetMemberDto getMyInfo(Long id) {
        // Member 엔티티를 조회
        Member member = memberRepository.findById(id).orElseThrow();

        GetAddressDto getAddressDto = new GetAddressDto(member.getAddress().getStreet(), member.getAddress().getDetail(), member.getAddress().getZipcode());

        GetMemberDto getMemberDto = GetMemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .address(getAddressDto).build();

        return getMemberDto;
    }

    @Override
    public ResponseEntity<GetMemberDto> updateMember(GetMemberDto getMemberDto, Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateMember(getMemberDto);

            memberRepository.save(member);

            GetMemberDto updatedMemberDto = GetMemberDto.builder()
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .build();

            return ResponseEntity.ok(updatedMemberDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    private GetAddressDto getAddressDto(Long memberId) {
        // 주소 정보를 반환하는 메소드 (기본 예시)
        Optional<Address> addressOptional = addressRepository.findById(memberId);
        if (addressOptional.isPresent()) {
            Address address = addressOptional.get();
            return GetAddressDto.builder()
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
