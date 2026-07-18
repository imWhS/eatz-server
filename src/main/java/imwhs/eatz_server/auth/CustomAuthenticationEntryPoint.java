package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.common.error.EatzErrorType;
import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자(액세스 토큰이 없는 클라이언트)가 인가되지 않은 URL로 요청한 경우, 예외를 처리하기 위해 호출됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String AUTH_FAILURE_REASON_ATTRIBUTE_KEY = "auth_failure_reason";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        EatzErrorType errorType;

//        Object authFailureAttribute = request.getAttribute(AUTH_FAILURE_REASON_ATTRIBUTE_KEY);
//
//        // attribute가 ErrorCodeAuth 타입인지 확인하고, 맞으면 해당 Enum 상수를 사용
//        if (authFailureAttribute instanceof EatzAuthErrorType) {
//            errorType = (EatzAuthErrorType) authFailureAttribute;
//            log.warn("인증을 실패했어요: {}", errorType.getCode());
//        } else {
//            // attribute가 없거나 ErrorCodeAuth 타입이 아닌 경우
//            // 이는 토큰 자체가 없었거나 AccessTokenFilter에서 특정 예외로 처리되지 않은 경우로 간주합니다.
//            errorType = EatzAuthErrorType.TOKEN_ACCESS_MISSING; // 기본 값: 토큰 누락
//            log.warn("인증을 실패했어요: 토큰 누락 또는 알 수 없는 처리 오류");
//        }

        errorType = EatzAuthErrorType.TOKEN_ACCESS_MISSING; // 기본 값: 토큰 누락
        log.warn("인증되지 않은 사용자(액세스 토큰이 없는 클라이언트)가 인가되지 않은 URL로 요청했어요. | HTTP 요청 URI: {}",
                request.getRequestURI());

        response.setContentType("application/json");
        response.setStatus(errorType.getStatus().value());
        ErrorResponse body = ErrorResponse.create(errorType);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
