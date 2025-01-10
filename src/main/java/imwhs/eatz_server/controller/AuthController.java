package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.auth.SignInRequestDto;
import imwhs.eatz_server.dto.auth.SignInResponseDto;
import imwhs.eatz_server.dto.auth.SignUpRequestDto;
import imwhs.eatz_server.exception.token.InvalidTokenException;
import imwhs.eatz_server.exception.token.MissingTokenException;
import imwhs.eatz_server.exception.token.TokenExpiredException;
import imwhs.eatz_server.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final TokenManager tokenManager;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Long>> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        Long userId = authService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        try {
            String refreshToken = getRefreshToken(request);

            String type = tokenManager.getType(refreshToken);
            String username = tokenManager.getUsername(refreshToken);
            String role = tokenManager.getRole(refreshToken);

            if (!Objects.equals(type, "refresh")) {
                throw new InvalidTokenException("리프레시 토큰이 존재하지 않습니다.");
            }

            if (tokenManager.isExpired(refreshToken)) {
                throw new TokenExpiredException("리프레시 토큰이 만료되었습니다.");
            }

            String accessToken = tokenManager.createAccessToken(username, role);
            response.setHeader("Authorization", "Bearer " + accessToken);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
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

}
