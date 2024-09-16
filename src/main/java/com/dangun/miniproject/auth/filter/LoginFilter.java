package com.dangun.miniproject.auth.filter;

import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private final static long ACCESS_TOKEN_EXPIRE_TIME = 60 * 10 * 1000L;
    private final static long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> requestBody = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            String email = requestBody.get("email");
            String password = requestBody.get("password");

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new BadCredentialsException("LoginFilter : Request JSON 요청 파싱 중 오류 발생", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                      FilterChain chain, Authentication authentication) throws IOException {
        String email = authentication.getName();

        String accessToken = jwtUtil.createJwt("accessToken", email, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtUtil.createJwt("refreshToken", email, REFRESH_TOKEN_EXPIRE_TIME);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("refreshToken", refreshToken));

        // response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> data = new HashMap<>();
        data.put("message", "Login Successful :)");
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        response.getWriter().write(new ObjectMapper().writeValueAsString(data));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> data = new HashMap<>();
        data.put("message", "Login Failed.. -> Id / Password invalid..");

        response.getWriter().write(new ObjectMapper().writeValueAsString(data));

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
