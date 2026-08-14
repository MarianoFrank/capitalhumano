package ar.edu.utn.frsf.capitalhumano.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// Servicio que maneja la generación, validación, extracción y cookies del token JWT.
@Service
public class JwtService {

    private static final String ACCESS_COOKIE_NAME = "jwt_capitalhumano";

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generarToken(String nombreUsuario, String rol) {
        return Jwts.builder()
                .claim("role", "ROLE_" + rol) // Guardamos el rol con el prefijo que usa Spring
                .subject(nombreUsuario)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extraerRol(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean esTokenValido(String token) {
        return !isTokenExpired(token);
    }

    public ResponseCookie generarJwtCookie(String jwt) {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, jwt)
                .httpOnly(true)
                .secure(false) // Pasar a true si usás HTTPS en producción
                .path("/")
                .maxAge(EXPIRATION_TIME / 1000)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie obtenerCleanJwtCookie() {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, "")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String extraerNombreUsuario(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
