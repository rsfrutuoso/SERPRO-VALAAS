package com.valaas.validator.test;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public Filter testAuthFilter() {
        return new HttpFilter() {
            @Override
            protected void doFilter(HttpServletRequest request, HttpServletResponse response, jakarta.servlet.FilterChain chain) throws IOException, ServletException {
                // In tests we accept X-Test-Client-Id header to simulate PLIN-authenticated client
                String clientId = request.getHeader("X-Test-Client-Id");
                if (clientId == null) {
                    clientId = "test-client";
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(clientId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                try {
                    chain.doFilter(request, response);
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }
        };
    }
}
