package com.dangun.miniproject.auth.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlackListService {

    private final Set<String> tokenBlackList = new HashSet<>();

    public void addBlackListToken(String token) {
        if (isBlackListToken(token)) {
            throw new RuntimeException("이미 블랙 리스트에 등록 된 토큰입니다.");
        }
        tokenBlackList.add(token);
    }

    public boolean isBlackListToken(String token) {
        return tokenBlackList.contains(token);
    }
}
