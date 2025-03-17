package imwhs.eatz_server.controller.admin;

import imwhs.eatz_server.service.EatzUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class AdminHomeController {
    
    private final EatzUserService userService;

    @RequestMapping("/hello-admin")
    public String adminHome() {
        return "admin/home";
    }
    
}
