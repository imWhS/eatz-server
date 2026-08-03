package imwhs.eatz_server.auth;

import imwhs.eatz_server.config.properties.JwtConfigProperties;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class TokenManager {

    private final JwtConfigProperties jwtConfigProperties;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(
                jwtConfigProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    public String getType(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload()
                .get("type", String.class);
    }

    public LocalDateTime getExpiration(String token) {
        System.out.println("TokenManager.getExpiration");
        Date expiration = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload()
                .getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 액세스 토큰을 생성합니다.
     */
    public String createAccessToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("type", "access")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfigProperties.getAccessExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     */
    public String createRefreshToken(String username, String role) {
        System.out.println("TokenManager.createRefreshToken");
        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfigProperties.getRefreshExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 리프레시 토큰 쿠키를 생성합니다.
     */
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);

        if (refreshToken != null) {
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (jwtConfigProperties.getRefreshExpirationTime() / 1000));
        } else {
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
        }

        return refreshTokenCookie;
    }

}
