package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.JwtProperties;
import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.auth.SignUpRequestDto;
import imwhs.eatz_server.exception.token.InvalidTokenException;
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
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final TokenManager tokenManager;

    private final JwtProperties jwtProperties;

    @PostMapping("/public/sign-up")
    public ResponseEntity<ApiResponse<Long>> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        Long userId = authService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }

    @PostMapping("/public/reissue")
    public ResponseEntity<ApiResponse<?>> reauthorization(HttpServletRequest request, HttpServletResponse response) {
        try {
            // TODO: 이전 리프레시 토큰 블랙리스트로 관리
            String refreshToken = getRefreshToken(request);
            String username = tokenManager.getUsername(refreshToken);
            String role = tokenManager.getRole(refreshToken);

            reissueAccessToken(response, refreshToken, username, role);
            reissueRefreshToken(response, username, role);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("리프레시 토큰이 만료됐습니다."));
        }
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            throw new InvalidTokenException("쿠키가 존재하지 않습니다.");
        }

        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), "RefreshToken")) {
                return cookie.getValue();
            }
        }

        throw new InvalidTokenException("리프레시 토큰이 존재하지 않습니다.");
    }

    private void reissueAccessToken(HttpServletResponse response, String refreshToken, String username, String role) {
        String type = tokenManager.getType(refreshToken);

        if (!Objects.equals(type, "refresh")) {
            throw new InvalidTokenException("토큰의 종류가 리프레시 토큰이 아닙니다.");
        }

        String accessToken = tokenManager.createAccessToken(username, role);
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    private void reissueRefreshToken(HttpServletResponse response, String username, String role) {
        String newRefreshToken = tokenManager.createRefreshToken(username, role);
        Cookie refreshTokenCookie = new Cookie("RefreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge((int) jwtProperties.getRefreshExpirationTime()); // 리프레시 토큰 유효 시간과 동일하게 설정
        response.addCookie(refreshTokenCookie);
    }

}
