package imwhs.eatz_server.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터입니다.<br/>
 * 요청 헤더에서 JWT 형식의 토큰의 존재 여부와 유효성을 확인한 후,
 * 인증 정보를 SecurityContext에 저장해 해당 요청을 처리하는 동안 인증 및 인가 정보를 유지합니다.
 * Spring Security의 OncePerRequestFilter를 상속해 요청을 받을 때마다 실행됩니다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 요청 시 포함된 토큰 값을 추출합니다.
        String token = extractToken(request);

        System.out.println("JwtAuthenticationFilter.doFilterInternal called");

        // 토큰이 존재하지 않으면, 다음 필터 체인으로 실행을 넘깁니다.
        if (token == null) {
            System.out.println("JwtAuthenticationFilter.doFilterInternal: 토큰이 존재하지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 존재하는 경우, 토큰의 유효성을 검증합니다.
        try {
            Authentication authentication = tokenManager.createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            System.out.println("JwtAuthenticationFilter.doFilterInternal: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("JwtAuthenticationFilter.doFilterInternal: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("지원하지 않거나, 잘못된 형식의 토큰입니다.");
        } catch (Exception e) {
            System.out.println("JwtAuthenticationFilter.doFilterInternal: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("유효하지 않은 토큰입니다.");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 토큰을 추출합니다.
     * @param request 요청 헤더.
     * @return 토큰.
     */
    private static String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        System.out.println("authorizationHeader = " + authorizationHeader);

        return authorizationHeader.substring(7);
    }

}
