package com.dangun.miniproject.filter;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UserDetailsDto;
import com.dangun.miniproject.jwt.JWTUtil;
import com.dangun.miniproject.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/login") || request.getRequestURI().equals("/") || request.getRequestURI().equals("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // Header 체크
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("요청 헤더에 토큰이 비어있습니다");
            setUnauthorizedResponse(request, response, filterChain);
            return;
        }

        String token = authorization.split(" ")[1];

        // expireTime 체크
        if (jwtUtil.isExpiredToken(token)) {
            log.warn("요청 헤더에 있는 토큰의 사용 시간이 만료 되었습니다.");
            setUnauthorizedResponse(request, response, filterChain);
            return;
        }

        // 유저 객체 추출
        String email = jwtUtil.getMemberEmail(token);
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            log.warn("사용자를 찾을 수 없습니다");
            setUnauthorizedResponse(request, response, filterChain);
            return;
        }

        // Jwt 검증
        UserDetailsDto customUserDetails = new UserDetailsDto(member);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void setUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        filterChain.doFilter(request, response);
    }
}
