package org.john.personal.urlshortify.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.TokenBlackListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;
    private final TokenBlackListRepository tokenBlacklistRepository;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Create a jwt token with claims - user-id and email
    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))// 12 hours
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    private boolean isTokenBlacklisted(String token) {
        try {
            return tokenBlacklistRepository.existsByToken(token);
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            // Fail secure - if we can't check blacklist, consider token invalid
            return true;
        }
    }


    public Optional<Claims> validateTokenAndGetClaims(String token) {
        if (isTokenBlacklisted(token)) {
            log.debug("Token found in blacklist");
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(claims);

        } catch (SecurityException | ExpiredJwtException | MalformedJwtException e) {
            logTokenError(e);
            return Optional.empty();
        }
    }

    private void logTokenError(RuntimeException e) {
        if (e instanceof ExpiredJwtException) {
            log.debug("Token has expired");
        } else if (e instanceof SignatureException) {
            log.warn("Invalid token signature");
        } else if (e instanceof MalformedJwtException) {
            log.warn("Malformed token");
        } else {
            log.error("Token validation error: {}", e.getMessage());
        }
    }

    public Long getUserIdFromToken(String token){
        Optional<Claims> optionalClaims = validateTokenAndGetClaims(token);
        if(optionalClaims.isPresent()){
            Claims claims = optionalClaims.get();
            return claims.get("userId", Long.class);
        }
        return null;
    }


}
