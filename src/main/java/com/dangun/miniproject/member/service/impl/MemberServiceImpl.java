package com.dangun.miniproject.member.service.impl;

import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.repository.AddressRepository;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.comment.repository.CommentRepository;
import com.dangun.miniproject.member.repository.MemberRepository;
import com.dangun.miniproject.member.service.MemberService;
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
    public GetMemberDto updateMember(GetMemberDto getMemberDto, Long id) {
        Member member = memberRepository.findById(id).orElseThrow();

        if (getMemberDto.getNickname() == null || getMemberDto.getNickname().trim().isBlank()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        member.updateMember(getMemberDto);

        memberRepository.save(member);

        GetMemberDto updatedMemberDto = GetMemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .address(GetAddressDto.fromEntity(member.getAddress()))
                .build();

        return updatedMemberDto;
    }

    @Override
    public GetAddressDto updateAddress(GetAddressDto getAddressDto, Long id) {
        Address address = addressRepository.findById(id).orElseThrow();

        if (getAddressDto.getStreet() == null || getAddressDto.getStreet().trim().isBlank()) {
            throw new IllegalArgumentException("주소를 입력해주세요.");
        }
        if (getAddressDto.getDetail() == null || getAddressDto.getDetail().trim().isBlank()) {
            throw new IllegalArgumentException("상세주소를 입력해주세요.");
        }
        if (getAddressDto.getZipcode() == null || getAddressDto.getZipcode().trim().isBlank()) {
            throw new IllegalArgumentException("우편번호를 입력해주세요.");
        }

        address.updateAddress(getAddressDto);

        addressRepository.save(address);

        GetAddressDto updatedAddressDto = GetAddressDto.builder()
                .street(address.getStreet())
                .detail(address.getDetail())
                .zipcode(address.getZipcode())
                .build();

        return updatedAddressDto;
    }


    private GetAddressDto getAddressDto(Long memberId) {
        // 주소 정보를 반환하는 메소드 (기본 예시)
        Optional<Address> addressOptional = addressRepository.findById(memberId);
            Address address = addressOptional.get();
            return GetAddressDto.builder()
                    .street(address.getStreet())
                    .detail(address.getDetail())
                    .zipcode(address.getZipcode())
                    .build();
    }

    @Override
    public boolean deleteMember(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);

            Member member = memberOptional.get();

            commentRepository.deleteInBatch(member.getComments());
            boardRepository.deleteInBatch(member.getBoards());
            addressRepository.delete(member.getAddress());
            memberRepository.delete(member);

            return true;
    }

}
