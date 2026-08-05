package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.dto.auth.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserUsernameDuplicationResponse;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import imwhs.eatz_server.service.auth.AuthService;
import imwhs.eatz_server.service.auth.AuthPasswordResetService;
import imwhs.eatz_server.service.eatzuser.EatzUserQueryService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@RestController
public class AuthController {

    private final AuthService authService;
    private final EatzUserQueryService userQueryService;
    private final AuthPasswordResetService passwordResetService;
    private final TokenManager tokenManager;

    @PostMapping("/sign-up")
    public Long registerUser(@Valid @RequestBody CreateEatzUserRequest request) {
        return authService.signUp(request.getUsername(), request.getEmail(), request.getPassword());
    }

    @GetMapping("/auth/check-username")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EatzUserUsernameDuplicationResponse> checkUsernameDuplication(@RequestParam String username) {
        boolean isDuplicated = userQueryService.checkUsernameDuplication(username);
        return ResponseEntity.ok(new EatzUserUsernameDuplicationResponse(isDuplicated));
    }

    @GetMapping("/auth/email-status")
    @ResponseStatus(HttpStatus.OK)
    public EmailAvailabilityResponse getUserEmailStatus(@RequestParam String email) {
        return userQueryService.getEmailStatus(email);
    }

    @PostMapping("/auth/reset-password/request")
    @ResponseStatus(HttpStatus.OK)
    public void requestPasswordReset(@Valid @RequestBody EmailVerificationCodeRequest request) {
        passwordResetService.requestEmailVerification(request.getEmail());
    }

    @GetMapping("/auth/reset-password/authorize-token")
    @ResponseStatus(HttpStatus.OK)
    public VerifyResetTokenResponse authorizePasswordReset(@RequestParam String emailVerificationToken) {
        return passwordResetService.authorizePasswordReset(emailVerificationToken);
    }

    @PostMapping("/auth/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ConfirmPasswordResetRequest request) {
        passwordResetService.reset(request.getAuthorizedToken(), request.getNewPassword());
    }

    @PostMapping("/reissue-token")
    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("토큰 재발급 처리를 시작할게요.");
            String refreshToken = getRefreshToken(request);
            String email = tokenManager.getUsername(refreshToken);
            String role = tokenManager.getRole(refreshToken);

            String accessToken = authService.reissueAccessToken(refreshToken, email, role);
            response.setHeader("Authorization", "Bearer " + accessToken);

            String newRefreshToken = authService.reissueRefreshToken(refreshToken, email, role);
            addRefreshTokenToCookie(response, newRefreshToken);
            log.info("액세스 토큰, 리프레시 토큰 재발급을 완료했어요.");
        } catch (BaseAuthenticationException e) {
            log.warn("토큰 재발급 처리 중 BaseAuthenticationException이 발생했어요: {}", e.getMessage());
            throw e;
        }  catch (AuthenticationException e) {
            log.warn("토큰 재발급 처리 중 AuthenticationException 발생했어요: {}", e.getMessage());
            throw new UnauthorizedEatzUserException();
        } catch (ExpiredJwtException e) {
            log.warn("토큰 재발급 처리 중 ExpiredJwtException이 발생했어요: {}", e.getMessage());
            throw new RefreshTokenExpiredException();
        } catch (Exception e) {
            log.error("토큰 재발급 처리 중 예상하지 못한 오류가 발생했어요.", e);
            throw new InternalServerErrorException();
        }
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);
        authService.logout(refreshToken);
        addRefreshTokenToCookie(response, null);
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            log.info("HTTP 요청에 쿠키가 존재하지 않아, 리프레시 토큰을 추출할 수 없어요.");
            throw new RefreshTokenMissingException();
        }

        List<Cookie> refreshTokenCookies = Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .toList();

        if (refreshTokenCookies.isEmpty()) {
            log.info("HTTP 요청의 쿠키에 리프레시 토큰이 없어요.");
            throw new RefreshTokenMissingException();
        }

        if (1 < refreshTokenCookies.size()) {
            log.info("HTTP 요청의 쿠키에 리프레시 토큰(RefreshToken) 쿠키가 2개 이상 중복돼요.");
            throw new RefreshTokenMissingException();
        }

        log.info("쿠키에서 리프레시 토큰을 성공적으로 추출했어요.");
        return refreshTokenCookies.get(0).getValue();
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = tokenManager.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);
    }

}
