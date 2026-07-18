package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.domain.RefreshToken;
import imwhs.eatz_server.dto.ErrorResponse;
import imwhs.eatz_server.dto.auth.LoginRequest;
import imwhs.eatz_server.dto.eatzuser.userdetail.EatzUserDetails;
import imwhs.eatz_server.exception.CredentialsInvalidExpiredException;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Stateless 환경의 RESTful API 서버에서 JSON 요청 데이터로 로그인 요청을 처리한 후 토큰을 발급하는 인증 필터입니다.
 * <ul>
 *     <li> HTTP 요청 데이터에서 JSON 형식의 로그인 정보(이메일, 암호)를 추출한 후,
 *          Spring Security의 AuthenticationManager를 통해 인증을 진행합니다. </li>
 *     <li> 인증에 성공하면 액세스 토큰을 HTTP 응답 헤더에, 리프레시 토큰은 HttpOnly 속성이 적용된 쿠키에 담아 응답 데이터로 설정합니다. </li>
 *     <li> 인증에 실패하면 즉시 필터 체인을 끊고 401 상태 코드와 함께 JSON 에러 메시지를 응답 데이터로 설정합니다. </li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ObjectMapper objectMapper;
    private final TokenManager tokenManager;
    private final JwtConfigProperties jwtConfigProperties;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * HTTP 요청에서 로그인(인증 요청) 정보를 추출한 후, 인증을 시도합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증된 Authentication. 로그인 요청한 사용자가 인증된 경우, 인증 및 권한 정보를 포함합니다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            // HTTP 요청에서 로그인(인증 요청) 정보를 추출합니다.
            LoginRequest loginRequest = parseLoginRequest(request);

            // 이메일, 암호로 사용자를 인증하기 위한 Authentication를 생성합니다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword());

            // AuthenticationManager를 통한 인증을 진행합니다.
            // 인증 성공 시 로그인 상태, 권한 등의 정보를 포함하는 Authentication를 생성 후 반환합니다.
            return authenticationManager.authenticate(authenticationToken);
        }  catch (IOException e) {
            log.warn("HTTP 요청에서 로그인 정보 파싱을 실패했어요. | {} - {}", e.getClass(), e.getMessage());
            // unsuccessfulAuthentication로 분기됩니다.
            throw new BadCredentialsException("올바르지 않은 로그인 요청이에요.");
        }
    }

    /**
     * AuthenticationManager를 통한 인증 성공 시 호출됩니다.<br/>
     * 인증 성공한 사용자의 정보로 액세스 토큰, 리프레시 토큰을 발급한 후 HTTP 응답 헤더에 추가합니다.
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authenticationResult) throws IOException, ServletException {
        EatzUserDetails eatzUserDetails = (EatzUserDetails) authenticationResult.getPrincipal();
        String email = eatzUserDetails.getUsername();
        String username = eatzUserDetails.getEatzUsername();
        String role = authenticationResult.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // HTTP 응답 헤더를 통해 액세스 토큰을 생성하니다.
        String accessToken = tokenManager.createAccessToken(email, role);
        response.setHeader("Authorization", "Bearer " + accessToken);

        // HTTP Only 쿠키를 통해 리프레시 토큰을 생성합니다.
        String refreshToken = tokenManager.createRefreshToken(email, role);
        log.info("리프레시 토큰을 생성했어요: {}", refreshToken);
        createRefreshTokenCookie(response, refreshToken);

        // 생성한 리프레시 토큰을 저장합니다.
        saveRefreshToken(refreshToken, email);

        log.info("로그인을 성공적으로 완료했어요. | 사용자 이메일 주소: {} | 사용자 이름: {}", email, username);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }

    private void saveRefreshToken(String refreshToken, String email) {
        LocalDateTime expiration = tokenManager.getExpiration(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(email, expiration, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge((int) jwtConfigProperties.getRefreshExpirationTime()); // 리프레시 토큰 유효 시간과 동일하게 설정
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }

    /**
     * AuthenticationManager를 통한 인증 실패 시 호출됩니다.<br/>
     * 로그인 요청 시 HTTP 요청과 함께 전달한 이메일 주소, 암호에 해당하는 사용자가 데이터베이스에 존재하지 않는 경우에 해당합니다.
     * 클라이언트에게 HTTP 응답으로 UNAUTHORIZED(401)를 전달합니다.
     */
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorResponse errorResponse = ErrorResponse.create(EatzAuthErrorType.CREDENTIALS_INVALID, failed.getMessage());
        log.error("로그인을 실패했어요. | {} ({})", failed.getMessage(), failed.getClass().getName());
//        if (failed instanceof BadCredentialsException) {
//            EatzAuthErrorType errorCode = EatzAuthErrorType.CREDENTIALS_INVALID;
//            errorResponse = ErrorResponse.create(errorCode);
//        }
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        BaseAuthenticationException exception = new CredentialsInvalidExpiredException();
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }

    /**
     * HTTP 요청에서 로그인 정보를 파싱합니다.
     * @param request HTTP 요청
     * @return 로그인 정보를 담은 LoginRequest
     * @throws IOException 파싱을 실패한 경우
     */
    private LoginRequest parseLoginRequest(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    }

}
