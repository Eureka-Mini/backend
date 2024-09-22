package com.dangun.miniproject.common.config;

import com.dangun.miniproject.auth.filter.JWTExceptionHandlerFilter;
import com.dangun.miniproject.auth.filter.JWTFilter;
import com.dangun.miniproject.auth.filter.LoginFilter;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import com.dangun.miniproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final TokenBlackListService tokenBlackListService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManagerBean();
        LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil);
        loginFilter.setFilterProcessesUrl("/auth/login");

        JWTFilter jwtFilter = new JWTFilter(jwtUtil, memberRepository, tokenBlackListService);
        JWTExceptionHandlerFilter jwtExceptionHandlerFilter = new JWTExceptionHandlerFilter();

        // Http Security Setting
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "error").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, LoginFilter.class)
                .addFilterBefore(jwtExceptionHandlerFilter, JWTFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/html/**", "/favicon.ico", "/index.html");
    }
}
