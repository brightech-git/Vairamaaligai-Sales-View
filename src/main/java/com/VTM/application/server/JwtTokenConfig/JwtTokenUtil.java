package com.VTM.application.server.JwtTokenConfig;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class JwtTokenUtil {

    private final String SECRET_KEY = "this-is-a-very-secure-key-that-is-at-least-64-bytes-long-12345678!@#$";
    private final Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    // Token expiration time in seconds (30 days)
    private final Long expirationTime = 30 * 24 * 60 * 60L;

    // Generate JWT token with user details and roles
    public String generateToken(UserDetails userDetails, Long userId, String email, String contact) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("email", email);
        claims.put("contact", contact);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // usually username or email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate token by checking username and expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Check if token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract username (subject) from token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract email from token claims
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    // Extract contact from token claims
    public String extractContact(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("contact", String.class);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Extract all claims from token
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract userId from token claims
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Integer userIdInt = claims.get("userId", Integer.class);
        return userIdInt != null ? userIdInt.longValue() : null;
    }

    // Extract roles list from token claims safely
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> (String) obj)
                    .toList();
        }
        return List.of(); // empty list if roles not present or wrong type
    }

    // Check if token contains a specific role
    public boolean hasRole(String token, String role) {
        return extractRoles(token).contains(role);
    }


}
