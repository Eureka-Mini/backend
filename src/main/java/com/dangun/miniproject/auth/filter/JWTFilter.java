package com.dangun.miniproject.auth.filter;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
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

        if (request.getRequestURI().startsWith("/auth/") || request.getRequestURI().equals("/") || request.getRequestURI().startsWith("/static/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.getWriter().write("authorization null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String accessToken = authorization.split(" ")[1];

        if (accessToken == null) {
            response.getWriter().write("accessToken null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            jwtUtil.isExpiredToken(accessToken);
        } catch (ExpiredJwtException e) {
            response.getWriter().write("accessToken expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getJwtCategory(accessToken);

        if (!category.equals("accessToken")) {
            response.getWriter().write("not accessToken");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getMemberEmail(accessToken);
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            response.getWriter().write("not found member");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UserDetailsDto customUserDetails = new UserDetailsDto(member);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
