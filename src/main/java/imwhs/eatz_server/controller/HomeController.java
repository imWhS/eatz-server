package imwhs.eatz_server.controller;
import imwhs.eatz_server.dto.eatzuser.EatzUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal EatzUserDetails userDetails) {
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        System.out.println("userDetails.getEmail() = " + userDetails.getEmail());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        System.out.println("userDetails.getAuthorities() = " + authorities);

        return "Eatz RESTful API 서버입니다.";
    }

}