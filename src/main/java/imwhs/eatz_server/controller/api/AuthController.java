package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.common.error.ErrorCodeAuth;
import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.dto.apiresponse.ApiResponse;
import imwhs.eatz_server.dto.auth.RequestEmailVerificationDto;
import imwhs.eatz_server.dto.auth.RequestVerificationCodeViaEmailDto;
import imwhs.eatz_server.dto.auth.CreateEatzUserDto;
import imwhs.eatz_server.exception.InvalidTokenExceptionOld;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
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

    @PostMapping("/sign-up/email-validation/send-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> sendCodeViaEmail(@RequestBody RequestVerificationCodeViaEmailDto dto) {
        String email = dto.getEmail();
        authService.sendVerificationCodeToEmail(email);
        return ApiResponse.success();
    }

    @PostMapping("/sign-up/email-validation/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> verifyEmail(@RequestBody RequestEmailVerificationDto dto) {
        authService.verifyEmail(dto.getEmail(), dto.getCode());
        return ApiResponse.success();
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> registerUser(@RequestBody @Valid CreateEatzUserDto dto) {
        Long userId = authService.signUp(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return ApiResponse.success(userId);
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
        } catch (AuthenticationException e) {
            throw new UnauthorizedEatzUserException();
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(ErrorCodeAuth.TOKEN_REFRESH_EXPIRED.getMessage()));
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
            throw new InvalidTokenExceptionOld("쿠키가 존재하지 않아요.");
        }

        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), "RefreshToken")) {
                return cookie.getValue();
            }
        }

        throw new InvalidTokenExceptionOld("리프레시 토큰이 존재하지 않아요.");
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
