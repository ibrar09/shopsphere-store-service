package com.shopsphere.store_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // TRACER 1: Print the exact path being hit
        System.out.println(">>> [STORE-SERVICE] Request incoming for path: " + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // TRACER 2: Find out if the Gateway is eating your header
            System.out.println(">>> [STORE-SERVICE] BLOCKED: No valid Bearer token found in headers.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            System.out.println(">>> [STORE-SERVICE] Bearer token found! Attempting decryption...");
            String token = authHeader.substring(7);

            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String role = claims.get("role", String.class);
            if (role != null && role.startsWith("ROLE_")) {
                role = role.substring(5);
            }

            Number userIdNumber = claims.get("userId", Number.class);
            Long userId = (userIdNumber != null) ? userIdNumber.longValue() : null;

            String finalAuthority = "ROLE_" + role;

            // TRACER 3: Confirm exact authorities
            System.out.println(">>> [STORE-SERVICE] SUCCESS: Token decrypted! UserID: " + userId + " | Authority: " + finalAuthority);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, Collections.singletonList(new SimpleGrantedAuthority(finalAuthority))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.err.println(">>> [STORE-SERVICE] FATAL JWT ERROR: " + e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}