package com.msd.spring_boot_rest_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;
import com.msd.spring_boot_rest_api.model.Role;

import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long JWT_EXPIRATION_MS = 86400000; // 1 day

    // Generate token
    public String generateToken(String email, Role role) {
        System.out.println("Generating token for email: " + email + ", role: " + role);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(JWT_SECRET) // <--- change here
                .compact();
    }    

    // Get email from token
    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Get role from token
    public Role getRoleFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        return Role.valueOf(role);
    }

    // Validate token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // invalid token
        }
        return false;
    }
}
