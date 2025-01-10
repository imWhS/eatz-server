package imwhs.eatz_server.auth;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class TokenManager {

    private final JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
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

    /**
     * 액세스 토큰을 생성합니다.
     */
    public String createAccessToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("type", "access")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     */
    public String createRefreshToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }



}
