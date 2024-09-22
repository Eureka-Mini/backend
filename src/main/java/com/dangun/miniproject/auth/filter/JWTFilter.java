package com.dangun.miniproject.auth.filter;

import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.auth.exception.exceptions.ReissueAccessTokenException;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final TokenBlackListService tokenBlackListService;
    private final static long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;
    private final static long ACCESS_TOKEN_EXPIRE_TIME_TEST = 20 * 1000L;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.startsWith("/auth/") || uri.equals("/") || uri.startsWith("/static/") || uri.startsWith("/resources/") || uri.matches(".*\\.(html|css|js|png|jpg|jpeg|ico)$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.getWriter().write("authorization null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String[] authParts = authorization.split(" ");
        if (authParts.length < 2 || authParts[1] == null || authParts[1].trim().isEmpty()) {
            response.getWriter().write("accessToken null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String accessToken = authParts[1];

        if (tokenBlackListService.isBlackListToken(accessToken)) {
            response.getWriter().write("accessToken is blackList contains.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            boolean isExpireAccess = jwtUtil.isExpiredTokenAccess(accessToken);

            if (isExpireAccess) {
                reissueAccessToken(request, response, filterChain);
                return;
            }
        } catch (JwtException e) {
            throw e;
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

    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ReissueAccessTokenException("refreshToken null");
        }

        try {
            jwtUtil.isExpiredTokenRefresh(refreshToken);
        } catch (ExpiredJwtException e) {
            response.getWriter().write("refreshToken expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getMemberEmail(refreshToken);
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UsernameNotFoundException("User not found");
        }

        String nickname = member.getNickname();

        String accessToken = jwtUtil.createJwtAccess("accessToken", email, nickname, ACCESS_TOKEN_EXPIRE_TIME);

        System.out.println("accessToken 재발급 성공! : " + accessToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        setAuthentication(member);

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Member member) {
        UserDetailsDto customUserDetails = new UserDetailsDto(member);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
