package com.dangun.miniproject.config;

import com.dangun.miniproject.filter.JWTFilter;
import com.dangun.miniproject.filter.LoginFilter;
import com.dangun.miniproject.jwt.JWTUtil;
import com.dangun.miniproject.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManagerBean();
        LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil);
        loginFilter.setFilterProcessesUrl("/login");

        JWTFilter jwtFilter = new JWTFilter(jwtUtil, memberRepository);

        // Http Security Setting
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/signup", "/").permitAll()
                        .anyRequest().permitAll())

                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)) // 로그인 시 세션 전체 삭제

                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, LoginFilter.class)

                .cors((cors) -> cors // cors 정책 설정
                        .configurationSource(new CorsConfigurationSource() {

                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 프론트 port 확인 후 변경 필요
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }));

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
}
