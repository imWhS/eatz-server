package imwhs.eatz_server.auth;

import imwhs.eatz_server.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT 형식의 토큰 생성과 토큰 내용 파싱 등과 관련한 작업을 담당하는 클래스입니다.
 */
@RequiredArgsConstructor
@Component
public class TokenManager {

    private final JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    /**
     * 사용자 인증 및 인가 정보를 담은 토큰을 생성합니다.
     * @param email 사용자 이메일
     * @param role 사용자 권한
     * @return 토큰
     */
    public String createToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role.toString());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpirationTime());
        String issuer = jwtProperties.getIssuer();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setIssuer(issuer)
                .signWith(this.getSecretKey())
                .compact();
    }

    /**
     * 토큰의 payload 정보를 Claims 객체로 반환합니다.
     * @param token 토큰.
     * @return Claims 객체.
     */
    public Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰으로 인증을 위한 객체 Authentication을 생성합니다.
     * @param token 토큰.
     * @return Authentication 객체.
     */
    public Authentication createAuthentication(String token) {
        Claims claims = parseClaims(token);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        return UsernamePasswordAuthenticationToken.authenticated(email, null, List.of(new SimpleGrantedAuthority(role)));
    }

}
