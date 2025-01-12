package imwhs.eatz_server.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
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
        if (authException instanceof InsufficientAuthenticationException) {
            writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "인증이 부족합니다. 로그인 정보가 필요합니다.");
        } else if (authException instanceof BadCredentialsException) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "잘못된 자격 증명입니다. 아이디와 비밀번호를 확인하세요.");
        } else if (authException instanceof AuthenticationCredentialsNotFoundException) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 없습니다. 로그인해주세요.");
        } else {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        ApiResponse<Map<String, String>> responseBody = ApiResponse.error(message);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
