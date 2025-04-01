package com.midou.tutorial.user.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class PasswordResetTokenService {

    private static final String RESET_SECRET_KEY = "a7fhJHqD9jF7W9skmTfu3sJx8HkBpZ9S36Qa5q8tBR8aT20kzGr5L4cKnYzjwA5c"; // Use a different secret key for reset tokens

    public String generatePasswordResetToken(Long userId) {
        return generatePasswordResetToken(new HashMap<>(), userId);
    }

    public String generatePasswordResetToken(Map<String, Object> extraClaims, Long userId) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(userId)) // Convert Long userId to String
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // Token valid for 10 minutes
                .claim("purpose", "reset-password") // Purpose of the token
                .signWith(getResetSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isPasswordResetTokenValid(String token, Long userId) {
        final Long extractedUserId = extractUsername(token);
        return (extractedUserId.equals(userId) && !isTokenExpired(token) && "reset-password".equals(extractClaim(token, claims -> claims.get("purpose"))));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Long extractUsername(String token) {
        return Long.valueOf(extractClaim(token, Claims::getSubject)); // Convert String back to Long
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getResetSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getResetSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(RESET_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
