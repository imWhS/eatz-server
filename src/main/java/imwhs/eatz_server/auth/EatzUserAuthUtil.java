package imwhs.eatz_server.auth;

import imwhs.eatz_server.dto.eatzuser.EatzUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class EatzUserAuthUtil {

    private EatzUserAuthUtil() {
        throw new AssertionError();
    }

    public static Long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    public static String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EatzUserDetails userDetails = (EatzUserDetails) authentication.getPrincipal();
        return userDetails.getEatzUsername();
    }

}