package com.example.kwordpocket.common.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .requestCache(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, cause) ->
                                sendError(
                                        response,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Authorization 헤더에 JWT가 필요합니다."
                                )
                        )
                        .accessDeniedHandler((request, response, cause) ->
                                sendError(
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "접근 권한이 없습니다."
                                )
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/signin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()

                        .requestMatchers(HttpMethod.GET, "/faqs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter filter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }
}
