package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.exception.token.InvalidTokenException;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import imwhs.eatz_server.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {

    private final ObjectMapper objectMapper;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (!(requestURI.matches("/logout$") && method.equals("POST"))) {
            // 로그아웃 요청이 아닌 경우, 필터 체인을 실행합니다.
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String refreshToken = getRefreshToken(request);
            Boolean isExist = refreshTokenRepository.existsByToken(refreshToken);

            if (!isExist) {
                throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
            }

            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.setStatus(HttpStatus.OK.value());
        } catch (InvalidTokenException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Map<String, String>> responseBody = ApiResponse.error(e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
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
