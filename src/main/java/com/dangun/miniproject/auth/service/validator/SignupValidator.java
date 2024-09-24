package com.dangun.miniproject.auth.service.validator;

import com.dangun.miniproject.auth.exception.DuplicateException;
import com.dangun.miniproject.auth.exception.InvalidInputException;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SignupValidator {

    private final MemberRepository memberRepository;

    private final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public void validateMember(GetMemberRequest memberReq) {
        validateEmailFormat(memberReq.getEmail());
        validatePassword(memberReq.getPassword());
        validateNickname(memberReq.getNickname());
        validateStreet(memberReq.getAddress().getStreet());
        validateDetail(memberReq.getAddress().getDetail());
        validateZipcode(memberReq.getAddress().getZipcode());

        isConflictEmail(memberReq.getEmail());
        isConflictNickname(memberReq.getNickname());
    }

    private void validateEmailFormat(String email) {
        if (!isEmailValid(email)) {
            throw new InvalidInputException("유효하지 않은 이메일 형식입니다.");
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new InvalidInputException("닉네임 값을 입력해주세요.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isBlank()) {
            throw new InvalidInputException("비밀번호를 입력해주세요.");
        }
    }

    private void validateStreet(String street) {
        if (street == null || street.trim().isBlank()) {
            throw new InvalidInputException("주소를 입력해주세요.");
        }
    }

    private void validateDetail(String detail) {
        if (detail == null || detail.trim().isBlank()) {
            throw new InvalidInputException("상세주소를 입력해주세요.");
        }
    }

    private void validateZipcode(String zipcode) {
        if (zipcode == null || zipcode.trim().isBlank()) {
            throw new InvalidInputException("우편번호를 입력해주세요.");
        }
    }

    private void isConflictEmail(String email) {
        Boolean isExistEmail = memberRepository.existsByEmail(email);
        if (isExistEmail) {
            throw new DuplicateException("이미 존재하는 이메일 입니다.");
        }
    }

    private void isConflictNickname(String nickname) {
        Boolean isExistNickname = memberRepository.existsByNickname(nickname);
        if (isExistNickname) {
            throw new DuplicateException("이미 존재하는 닉네임 입니다.");
        }
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
