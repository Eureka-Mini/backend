package com.dangun.miniproject.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTExceptionHandlerFilter extends OncePerRequestFilter {

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        try {
            filterChain.doFilter(request, response);
        } catch(io.jsonwebtoken.security.SignatureException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰 서명입니다.");
        } catch(io.jsonwebtoken.JwtException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
        } catch(Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWTExceptionHandlerFilter 서버에러");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(message);
        response.setStatus(status);
    }
}
