package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.exception.token.InvalidTokenException;
import imwhs.eatz_server.exception.token.TokenExpiredException;
import imwhs.eatz_server.service.EatzUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 클라이언트의 HTTP 요청에서 JWT 형식의 액세스 토큰을 추출하고, 액세스 토큰의 유효성을 확인해 요청을 보낸 사용자의 인증 상태를 확인합니다.<br/>
 * 액세스 토큰이 유효하다면, Spring Security에게 인증된 사용자임을 인식시키기 위해 SecurityContext에 인증 정보를 저장합니다.
 */
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    private final EatzUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        // HTTP 요청에 JWT 형식의 액세스 토큰을 담은 Authorization 헤더가 존재하는지 확인합니다.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 액세스 토큰 값을 가진 헤더가 없거나, 올바른 JWT 형식의 토큰이 아닌 경우
            // 요청을 필터 체인에 넘기고 필터 실행을 종료합니다.
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 액세스 토큰 값만 추출합니다.
        String accessToken = authorizationHeader.substring("Bearer ".length());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        try {
            // 액세스 토큰의 유효성을 확인합니다.
            if (tokenManager.isExpired(accessToken)) {
                throw new TokenExpiredException("액세스 토큰이 만료됐습니다.");
            }

            if (!Objects.equals(tokenManager.getType(accessToken), "access")) {
                throw new InvalidTokenException("유효한 형식의 액세스 토큰이 아닙니다.");
            }

            // 토큰에서 username, 역할 정보를 추출합니다.
            String username = tokenManager.getUsername(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 요청을 필터 체인에 넘깁니다.
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            // 유효한 액세스 토큰이지만, 액세스 토큰을 발급한 사용자 정보가 존재하지 않는 경우 더 이상 요청을 필터 체인에 넘기지 않습니다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Map<String, String>> responseBody = ApiResponse.error("액세스 토큰을 발급한 사용자 정보가 존재하지 않습니다.");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (AuthenticationException e) {
            // 유효하지 않은 액세스 토큰인 경우 더 이상 요청을 필터 체인에 넘기지 않습니다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Map<String, String>> responseBody = ApiResponse.error(e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Map<String, String>> responseBody = ApiResponse.error(e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            throw e;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Map<String, String>> responseBody = ApiResponse.error(e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        }
    }

}
