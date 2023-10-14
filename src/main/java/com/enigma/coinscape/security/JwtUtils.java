package com.enigma.coinscape.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtUtils {

    @Value("${coinscape.jwt.secret}")
    private String jwtSecret;

    @Value("${coinscape.jwt.expiration}")
    private Long jwtExpiration;

    public String getEmailByToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .compact();
    }

    public boolean validateJwtToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e){
            log.error("Invalid JWT token {}", e.getMessage());
        } catch (ExpiredJwtException e){
            log.error("JWT token is expired {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token {}", e.getMessage());
        } catch (IllegalArgumentException e){
            log.error("JWT claim string is empty {}", e.getMessage());
        }
        return false;
    }

}

