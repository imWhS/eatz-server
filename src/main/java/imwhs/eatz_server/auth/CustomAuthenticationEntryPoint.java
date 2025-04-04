package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.common.error.ErrorCodeAuth;
import imwhs.eatz_server.dto.apiresponse.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = "사용자 인증에 실패했습니다.";

        if (authException instanceof InsufficientAuthenticationException) {
            message = "사용자 인증 정보가 필요합니다.";
        }

        response.setContentType("application/json");
        ErrorCodeAuth errorCode = ErrorCodeAuth.TOKEN_ACCESS_MISSING;
        response.setStatus(errorCode.getStatus().value());
        ApiResponse<Map<String, String>> responseBody = ApiResponse.error(errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
