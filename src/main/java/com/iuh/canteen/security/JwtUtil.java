package com.iuh.canteen.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Use HS512 consistently for both key generation and token signing
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration}")
    private long expirationTime; // Set this in your application.properties or application.yml

    public String generateToken(String username) {

        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Use injected expiration time
                   .signWith(key) // Use the key directly
                   .compact();
    }

    public String extractUsername(String token) {
        // Update to the parserBuilder to avoid deprecated method
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean validateToken(String token) {

        try {
            // Update to the parserBuilder
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            System.err.println("validateToken true");
            return true;
        } catch (Exception e) {
            System.err.println("validateToken false: " + e.getMessage());
            return false;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extract JWT without "Bearer "
        }
        return null;
    }
}
