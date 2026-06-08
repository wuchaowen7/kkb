package com.ism.common.config;

import com.ism.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {});
        
        // 如果没有UserDetailsService（如intelligence服务），允许所有请求
        if (userDetailsService == null) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/login", "/api/auth/captcha", "/api/auth/avatar", "/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/avatar/**").permitAll()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }
        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String path = request.getRequestURI();
                if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/captcha") ||
                    (path.startsWith("/api/auth/avatar") && request.getMethod().equals("GET")) || path.startsWith("/doc.html") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        if (jwtUtil.validateToken(token)) {
                            String username = jwtUtil.getUsername(token);
                            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                            if (userDetailsService != null) {
                                authorities = userDetailsService.loadUserByUsername(username)
                                    .getAuthorities().stream()
                                    .filter(a -> a instanceof SimpleGrantedAuthority)
                                    .map(a -> (SimpleGrantedAuthority) a)
                                    .toList();
                            }
                            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    username, null, authorities);
                            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } catch (Exception ignored) {}
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}