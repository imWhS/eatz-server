package imwhs.eatz_server.controller;

import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.auth.RequestEmailVerificationDto;
import imwhs.eatz_server.dto.auth.RequestVerificationCodeViaEmailDto;
import imwhs.eatz_server.dto.auth.CreateEatzUserDto;
import imwhs.eatz_server.exception.InvalidTokenException;
import imwhs.eatz_server.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    private final TokenManager tokenManager;

    private final JwtConfigProperties jwtConfigProperties;

    /*
    1. iOS - 가입 버튼 탭
    2. iOS - 가입 화면 진입
    3. iOS - 이메일 주소 입력
    4. Server - 이메일 유효성 검증* (validateUserEmail)
    5. iOS - 이메일 인증 버튼 탭
    6. Server - 이메일 전송* (sendVerificationNumberToEmail)
    7. iOS - 이메일 인증 번호 입력
    8. Server - 인증 번호 유효성 검증* (validateVerificationNumberViaEmail)
    9. iOS - username, password 입력
    10. Server - signUp*
     */
    @PostMapping("/sign-up/email-validation/send-code")
    public ResponseEntity<ApiResponse<String>> sendCodeViaEmail(@RequestBody RequestVerificationCodeViaEmailDto dto) {
        String email = dto.getEmail();
        authService.sendVerificationCodeToEmail(email);
        return ResponseEntity.ok(ApiResponse.success(email + "로 인증 코드를 전송했어요."));
    }

    @PostMapping("/sign-up/email-validation/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody RequestEmailVerificationDto dto) {
        authService.verifyEmail(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.success("이메일 주소 인증을 완료했어요."));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Long>> registerUser(@RequestBody @Valid CreateEatzUserDto dto) {
        Long userId = authService.signUp(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userId));
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<ApiResponse<?>> reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = getRefreshToken(request);
            String email = tokenManager.getUsername(refreshToken);
            String role = tokenManager.getRole(refreshToken);

            String accessToken = authService.reissueAccessToken(refreshToken, email, role);
            response.setHeader("Authorization", "Bearer " + accessToken);

            String newRefreshToken = authService.reissueRefreshToken(refreshToken, email, role);
            addRefreshTokenToCookie(response, newRefreshToken);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("리프레시 토큰이 만료됐어요."));
        }
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);
        authService.logout(refreshToken);
        addRefreshTokenToCookie(response, null);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            throw new InvalidTokenException("쿠키가 존재하지 않아요.");
        }

        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), "RefreshToken")) {
                return cookie.getValue();
            }
        }

        throw new InvalidTokenException("리프레시 토큰이 존재하지 않아요.");
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);

        if (refreshToken != null) {
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setMaxAge((int) jwtConfigProperties.getRefreshExpirationTime()); // 리프레시 토큰 유효 시간과 동일하게 설정
        } else {
            refreshTokenCookie.setMaxAge(0);
        }

        response.addCookie(refreshTokenCookie);
    }

}
