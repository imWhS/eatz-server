package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.domain.RefreshToken;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserDetails;
import imwhs.eatz_server.exception.token.InvalidTokenException;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final TokenManager tokenManager;

    private final JwtProperties jwtProperties;

    // SRP를 준수하기 위해 SecurityConfig에서 스프링 빈으로 등록한 AuthenticationManager를 생성자로 주입 받습니다.
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * HTTP 요청에서 로그인 정보를 추출한 후, 인증을 시도합니다.
     * @param request HTTP 요청 객체.
     * @param response HTTP 응답 객체.
     * @return 인증된 Authentication 객체. 로그인 요청한 사용자가 인증된 경우, 인증 및 권한 정보를 포함합니다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            // HTTP 요청에서 로그인 정보를 추출합니다.
            LoginRequest loginRequest = parseLoginRequest(request);
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // 이메일, 비밀 번호로 사용자를 인증하기 위한 Authentication 객체를 생성합니다.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

            // AuthenticationManager를 통한 인증을 진행합니다. 인증 성공 시 로그인 상태, 권한 등의 정보를 Authentication에 포함시킵니다.
            // 이후 토큰 발급 등의 과정에서의 인증 및 권한 확인을 위해 Authentication을 SecurityContext에 저장합니다.
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            // HTTP 요청에서 로그인 정보를 파싱하는 과정에서 오류가 발생한 경우, 예외를 던집니다.
            throw new RuntimeException(e);
        }
    }

    /**
     * AuthenticationManager를 통한 인증 성공 시 호출됩니다.<br/>
     * 사용자의 인증 정보로 토큰을 발급한 후 HTTP 응답 헤더에 추가합니다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        EatzUserDetails eatzUserDetails = (EatzUserDetails) authentication.getPrincipal();
        String email = eatzUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // HTTP 응답 헤더를 통해 액세스 토큰을 발급합니다.
        String accessToken = tokenManager.createAccessToken(email, role);
        response.setHeader("Authorization", "Bearer " + accessToken);

        // HTTP Only 쿠키를 통해 리프레시 토큰을 발급합니다.
        String refreshToken = tokenManager.createRefreshToken(email, role);
        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge((int) jwtProperties.getRefreshExpirationTime()); // 리프레시 토큰 유효 시간과 동일하게 설정
        response.addCookie(refreshTokenCookie);

        // 클라이언트에 발급한 리프레시 토큰을 저장합니다.
        LocalDateTime expiration = tokenManager.getExpiration(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(email, expiration, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        response.setStatus(HttpStatus.OK.value());
    }

     /**
     * AuthenticationManager를 통한 인증 실패 시 호출됩니다.<br/>
     * 로그인 요청 시 HTTP 요청과 함께 전달한 이메일 주소, 비밀 번호에 해당하는 사용자가 데이터베이스에 존재하지 않는 경우에 해당합니다.
     * 클라이언트에게 HTTP 응답으로 UNAUTHORIZED(401)를 전달합니다.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiResponse<Map<String, String>> responseBody = ApiResponse.error(failed.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    /**
     * HTTP 요청에서 로그인 정보를 파싱합니다.
     * @param request HTTP 요청.
     * @return 로그인 정보를 담은 LoginRequest 객체.
     * @throws IOException 파싱을 실패했을 때 던지는 예외.
     */
    private LoginRequest parseLoginRequest(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }

}
