package imwhs.eatz_server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import imwhs.eatz_server.dto.apiresponse.ApiResponse;
import imwhs.eatz_server.service.EatzUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * 모든 HTTP 요청을 대상으로 JWT 형식의 액세스 토큰이 존재하는지 확인합니다.
 * 액세스 토큰이 존재한다면, 액세스 토큰의 유효성을 확인해 요청을 보낸 사용자의 인증 상태를 확인하는 인증 절차를 진행합니다..<br/>
 * 액세스 토큰이 존재하지 않는다면, 요청을 필터 체인에 넘깁니다. 즉, 인증이 필요 없는 API를 요청한 경우 액세스 토큰 관련 검증을 진행하지 않습니다.<br/>
 * 액세스 토큰이 유효하다면, Spring Security에게 인증된 사용자임을 인식시키기 위해 SecurityContext에 인증 정보를 저장합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    private final EatzUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        // HTTP 요청에 JWT 형식의 액세스 토큰을 담은 Authorization 헤더가 존재하는지 확인합니다.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("HTTP 요청에 Authorization 헤더가 존재하지 않아서 인증을 진행하지 않을게요!");
            // 액세스 토큰 값을 가진 헤더가 없거나, JWT 형식의 토큰이 올바르지 않은 경우
            // 요청을 필터 체인에 넘기고 인증 절차를 더 이상 진행하지 않습니다.
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 액세스 토큰을 추출합니다.
        String accessToken = authorizationHeader.substring("Bearer ".length());

        try {
            String username = tokenManager.getUsername(accessToken);



            // 토큰이 JWT 형식을 준수하지만, access(액세스 토큰) 타입이 아닌 경우 인증 절차를 진행하지 않습니다.
            if (!Objects.equals(tokenManager.getType(accessToken), "access")) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "토큰 타입이 access가 아닙니다.");
                return;
            }

            // 유효한 액세스 토큰인 경우, 사용자 인증 정보를 해당 클라이언트 요청에 대해서만 일시적으로 세선에 설정합니다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("해당 요청의 세션에 사용자 '{}'의 인증 정보를 성공적으로 저장했어요!", username);
        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "만료된 액세스 토큰입니다.");
            return;
        } catch (JwtException e) {
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.");
            return;
        }  catch (Exception e) {
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 내부에서 오류가 발생했습니다.");
            return;
        }

        // 요청을 필터 체인에 넘깁니다.
        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.error("해당 HTTP 요청에 대한 액세스 토큰의 유효성 검증을 실패했어요: {}", message);
        response.setContentType("application/json");
        response.setStatus(status);
        ApiResponse<Object> body = ApiResponse.error(message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
