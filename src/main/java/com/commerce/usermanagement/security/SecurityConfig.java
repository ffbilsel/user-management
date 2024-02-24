package com.commerce.usermanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final JwtTokenUtil jwtTokenUtil;

    public static final String HEADER_PREFIX = "Bearer ";
    public final Set<String> ALLOWED_URLS = new HashSet<>(List.of("/user/register", "/login"));
    public final String ALLOWED_PREFIXES = "/swagger|/api-docs";
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.cors().and().csrf().disable()
                .authorizeExchange()
                .pathMatchers("/user/register").permitAll()
                .pathMatchers("/api-docs/**").permitAll()
                .pathMatchers("/swagger/**").permitAll()
                .pathMatchers("/**").authenticated()
                .and().formLogin()
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .and().build();
    }

    @Bean
    public WebFilter customFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {

            String path = exchange.getRequest().getPath().value();
            String token = resolveToken(exchange.getRequest());

            if (path.matches("(" + ALLOWED_PREFIXES +").*") || ALLOWED_URLS.contains(path) || jwtTokenUtil.validateAccessToken(token)) {
                return chain.filter(exchange);
            }

            return exchange.getResponse().setComplete();
        };
    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
