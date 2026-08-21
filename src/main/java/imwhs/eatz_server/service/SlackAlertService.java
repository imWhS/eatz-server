package imwhs.eatz_server.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SlackAlertService {

    private final RestTemplate restTemplate;

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    public void sendErrorMessage(Exception e, HttpServletRequest request) {
        // MDC 로깅에 추가했던 요청 클라이언트 및 사용자 관련 정보를 가져옵니다.
        String username = MDC.get("username");
        String clientIp = MDC.get("clientIp");
        String userAgent = MDC.get("userAgent");

        String message = String.format(
                "🚨 *[EATZ 서버 오류]* 🚨\n" +
                        "• *사용자 이름*: %s\n" +
                        "• *요청 IP 주소*: %s\n" +
                        "• *요청 기기(클라이언트)*: %s\n" +
                        "• *요청 URI*: %s\n" +
                        "• *예외 메시지*: %s",
                username != null ? username : "guest",
                clientIp != null ? clientIp : "unknown",
                userAgent != null ? userAgent : "unknown",
                request.getRequestURI(),
                e.getMessage()
        );

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);

            restTemplate.postForEntity(webhookUrl, payload, String.class);
        } catch (Exception ex) {
            log.error("Slack으로 알림 메시지를 전송하지 못했어요.", ex);
        }
    }

}
