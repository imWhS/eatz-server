package imwhs.eatz_server.auth;

import imwhs.eatz_server.dto.eatzuser.userdetail.EatzUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class EatzUserAuth {

    private EatzUserAuth() {
        throw new AssertionError();
    } // TODO

    public static Optional<Long> getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getId());
        }
        return Optional.empty();
    }

    public static Optional<String> getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getUsername());
        }
        return Optional.empty();
    }

    public static Optional<String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getEatzUsername());
        }
        return Optional.empty();
    }

    private static boolean isAuthenticated(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                // 인증되지 않은 사용자(게스트)가 public 엔드포인트 URL로 요청하면, Spring Security가 "anonymousUser"로 설정하기 때문에
                // 이 경우에도 미인증 상태로 설정하기 위해 조건을 추가합니다.
                !(authentication.getPrincipal() instanceof String);
    }

}