package imwhs.eatz_server.auth;

import imwhs.eatz_server.exception.TokenAccessExpiredException;
import imwhs.eatz_server.exception.TokenAccessInvalidException;
import imwhs.eatz_server.service.eatzuser.EatzUserDetailsService;
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
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Stateless 환경의 서버 HTTP 요청마다 JWT 액세스 토큰의 유효성을 검증하고, 세션을 인증 상태로 설정하는 필터입니다.
 * 모든 HTTP 요청을 대상으로 JWT 형식의 액세스 토큰의 존재 유무를 확인하고 유효성을 검증합니다.
 * 액세스 토큰이 유효하다면, 이미 인증 완료된 사용자임을 세션에 저장합니다.
 * <ul>
 *     <li> HTTP 요청을 보낸 사용자의 인증 상태를 확인하는 인증 절차를 진행하기 위해,
 *          Authorization 헤더로부터 액세스 토큰을 추출해 해당 토큰의 유효성을 확인합니다. </li>
 *     <li> HTTP 요청에 Authorization 헤더가 존재하지 않는다면, 유효성을 검증할 토큰이 없으므로 요청을 필터 체인에 넘깁니다. </li>
 *     <li> HTTP 요청 URI의 public 여부와 관계 없이, 클라이언트가 HTTP 요청에 Authorization 헤더를 포함시킨 경우에는
 *          '이미 인증된 사용자'로서 요청한 것으로 간주해 액세스 토큰의 유효성을 검증합니다. </li>
 *     <li> 액세스 토큰이 유효하지 않거나 검증 중 오류가 발생하면, 필터 체인을 끊고 401 상태 코드를 응답합니다. </li>
 *     <li> 액세스 토큰이 유효하다면, Spring Security 계층에서 사용자 인증 정보를 해당 HTTP 요청의 세션 동안 유지하도록
 *          SecurityContext에 사용자 인증 정보를 설정합니다. </li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = BEARER_PREFIX.length();

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TokenManager tokenManager;
    private final EatzUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // HTTP 요청으로부터 Authorization 헤더를 가져옵니다.
        String authorizationHeader = request.getHeader("Authorization");

        String requestUri = request.getRequestURI();

        // Authorization 헤더가 JWT 형식의 토큰을 담고 있는지 확인합니다.
        if (!hasBearerToken(authorizationHeader)) {
            log.info("방금 받은 HTTP 요청에 JWT 기반 토큰이 없어요. | HTTP 요청 URI: {}", requestUri);
            // 헤더가 토큰을 담고 있지 않거나, JWT 기반 토큰이 아닌 경우 요청을 필터 체인의 다음 필터로 넘겨
            // 토큰 유효성 검증을 더 이상 진행하지 않습니다. Spring Security는 해당 세션에 AnonymousAuthenticationToken를 설정한 후
            // 필터 체인의 마지막 부분인 AuthorizationFilter에서 URL(엔드포인트)에 따른 인가 과정을 진행합니다.
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에 JWT 형식의 토큰이 담겨있다면, 해당 액세스 토큰을 추출합니다.
        String accessToken = authorizationHeader.substring(BEARER_LENGTH);

        // 액세스 토큰 유효성을 검증하고, 유효한 경우 사용자 인증 정보를 설정합니다.
        try {
            // 액세스 토큰 유형의 JWT 토큰이 아닌 경우, 토큰 유효성 검증을 더 이상 진행하지 않고 필터 체인을 끊습니다.
            if (!"access".equals(tokenManager.getType(accessToken))) {
                log.warn("방금 받은 HTTP 요청의 JWT 기반 토큰 유형이 올바르지 않아요. | HTTP 요청 URI: {}", requestUri);
                throw new TokenAccessInvalidException();
            }

            // 액세스 토큰으로 사용자 정보를 조회하면서 유효성을 검증합니다. 액세스 토큰은 인증(로그인)을 성공한 경우에만 발급되기 떄문에,
            // 액세스 토큰이 만료 또는 유효하지 않은 상태가 아닌 경우 이전에 성공적으로 로그인한 사용자가 요청한 것으로 간주합니다.
            // 그래서, 해당 클라이언트 요청에 대해서만 일시적으로 세션(현재 스레드)을 통해 사용자 정보를 관리하기 위해 인증 객체를 설정합니다.
            String username = tokenManager.getUsername(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("방금 받은 HTTP 요청의 세션에 사용자의 인증 정보를 성공적으로 설정했어요! | HTTP 요청 URI: {} | 사용자 이름: {}",
                    requestUri, username);
        } catch (ExpiredJwtException e) {
            log.info("액세스 토큰이 만료됐어요. | HTTP 요청 URI: {} | {}", requestUri, e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, new TokenAccessExpiredException());
            return;
        } catch (JwtException e) {
            log.warn("유효하지 않은 액세스 토큰을 포함하는 HTTP 요청이에요. | HTTP 요청 URI: {} | {} | {}",
                    requestUri, e.getClass().getSimpleName(), e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, new TokenAccessInvalidException());
            return;
        }  catch (Exception e) {
            log.warn("액세스 토큰 필터 처리 중 서버 내부 오류가 발생했어요: {}", e.getMessage());
//            writeErrorResponse(response, EatzAuthErrorType.TOKEN_ACCESS_INVALID);
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        // 요청을 필터 체인의 다음 필터로 넘깁니다.
        filterChain.doFilter(request, response);
    }

//    private void writeErrorResponse(HttpServletResponse response, EatzErrorType errorType) throws IOException {
//        log.error("해당 HTTP 요청에 대한 액세스 토큰의 유효성 검증을 실패했어요. 요청 처리를 중단할게요. | {}", errorType.getMessage());
//        response.setContentType("application/json");
//        response.setStatus(errorType.getStatus().value());
//        ErrorResponse body = ErrorResponse.create(errorType);
//        response.getWriter().write(objectMapper.writeValueAsString(body));
//    }

    private boolean hasBearerToken(String header) {
        return header != null && header.startsWith(BEARER_PREFIX);
    }

}
