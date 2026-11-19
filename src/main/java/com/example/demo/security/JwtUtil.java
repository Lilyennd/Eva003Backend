package com.example.demo.security;

import com.example.demo.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expiracionMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms:7200000}") long expiracionMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generar(Usuario u) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(u.getCorreo())
                .claim("id", u.getIdUsuario())
                .claim("rol", u.getRol().getNombreRol().toUpperCase())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(key)
                .compact();
    }

    public Claims leer(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String renovar(String tokenViejo) {
        Claims claims = leer(tokenViejo);
        Date ahora = new Date();
        return Jwts.builder()
                .subject(claims.getSubject())
                .claim("id", claims.get("id"))
                .claim("rol", claims.get("rol"))
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(key)
                .compact();
    }
}
