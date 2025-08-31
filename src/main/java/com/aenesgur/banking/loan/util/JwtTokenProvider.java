package com.aenesgur.banking.loan.util;

import com.aenesgur.banking.loan.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationInMs;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Optional<UUID> customerIdOptional = userDetails.getCustomerId();

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", roles)
                .setIssuedAt(new Date())
                .setExpiration(expireDate);

        customerIdOptional.ifPresent(id -> jwtBuilder.claim("customerId", id));

        String token = jwtBuilder.signWith(key()).compact();
        return token;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class);
    }

    public String getCustomerIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("customerId", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | IllegalArgumentException ex) {
            log.info("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }
}
