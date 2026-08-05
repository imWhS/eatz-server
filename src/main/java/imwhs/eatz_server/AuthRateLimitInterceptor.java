package imwhs.eatz_server;

import imwhs.eatz_server.exception.AuthRateLimitExceededException;
import imwhs.eatz_server.service.auth.AuthRateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthRateLimitInterceptor implements HandlerInterceptor {

    private final AuthRateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청한 클라이언트의 IP 주소를 추출합니다.
        // 모든 요청이 Nginx 프록시를 거쳐서 들어오기 때문에, Nginx로부터 전달 받은 원본 IP 헤더(X-Forwarded-For)를 가장 먼저 확인해야 합니다.
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        // IP 주소에 해당하는 bucket을 가져옵니다.
        Bucket bucket = rateLimiterService.resolveBucket(clientIp);

        // 토큰 1개를 사용 시도해봅니다.
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 토큰 1개가 정상 소비됐다면, 컨트롤러로 넘어가서 계속 진행할 수 있도록 인터셉터를 통과시킵니다.
            log.info("AuthRateLimit | 가입을 요청한 클라이언트의 IP 주소: {} | 남은 토큰 수: {}", clientIp, probe.getRemainingTokens());
            return true;
        } else {
            // 더 이상 소비할 수 있는 토큰이 없다면, 429 에러(Too Many Requests)를 반환하고 더 이상 진행하지 않습니다.
            log.warn("AuthRateLimit | 가입을 요청한 클라이언트의 IP 주소: {} | 소비할 수 있는 토큰이 없어요. 더 이상 요청을 처리하지 않아요.", clientIp);
            throw new AuthRateLimitExceededException();
        }
    }
}
