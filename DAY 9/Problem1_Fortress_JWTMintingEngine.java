
package com.fortress;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;


class JwtProvider {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "my-super-secret-key-that-must-be-at-least-32-bytes-long-for-HS256".getBytes()
    );

    private static final long EXPIRATION_MS = 15 * 60 * 1000; 



    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)                   
                .claim("authorities", roles)         
                .issuedAt(now)                       
                .expiration(expiration)                
                .signWith(secretKey)                   
                .compact();
    }

    public String extractUsername(String token) {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)   
                .build()
                .parseSignedClaims(token)   
                .getPayload();          

        return claims.getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("authorities", List.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}


class Problem1Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 9 - Problem 1: Fortress JWT Minting Engine ===");
        System.out.println();

        JwtProvider jwtProvider = new JwtProvider();

        String token = jwtProvider.generateToken("john_doe", List.of("ROLE_USER", "ROLE_ADMIN"));
        System.out.println("Generated Token:");
        System.out.println(token);
        System.out.println();

        String username = jwtProvider.extractUsername(token);
        System.out.println("Extracted Username: " + username);

        List<String> roles = jwtProvider.extractRoles(token);
        System.out.println("Extracted Roles: " + roles);

        System.out.println("Token Valid: " + jwtProvider.isTokenValid(token));
        System.out.println("Tampered Token Valid: " + jwtProvider.isTokenValid(token + "tampered"));

        System.out.println();
        System.out.println("Key Points:");
        System.out.println("1. Keys.hmacShaKeyFor() -> modern, secure key generation");
        System.out.println("2. No deprecated signWith(String) -> uses SecretKey object");
        System.out.println("3. Claims contain: sub (username), authorities (roles), iat, exp");
        System.out.println("4. Parser auto-throws ExpiredJwtException / SignatureException");
    }
}
