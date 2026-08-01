package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.auth.EmailVerificationCodeResponse;
import imwhs.eatz_server.dto.auth.EmailVerificationRequest;
import imwhs.eatz_server.dto.auth.EmailZonedVerificationCodeRequest;
import imwhs.eatz_server.service.auth.AuthEmailVerificationService;
import imwhs.eatz_server.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/sign-up")
@RestController
public class SignUpController {

    private final AuthService authService;
    private final AuthEmailVerificationService emailVerificationService;

    @GetMapping("/email-validation/status")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: 인증됨
    public void checkEmailVerificationStatus(@RequestParam String email) {
        authService.checkEmailVerificationStatus(email);
    }

    @PostMapping("/email-validation")
    @ResponseStatus(HttpStatus.OK)
    public EmailVerificationCodeResponse sendVerificationCodeViaEmail(
            @Valid @RequestBody EmailZonedVerificationCodeRequest request) {
        return emailVerificationService.sendCode(request.getEmail(), request.getTimeZoneId());
    }

    @PostMapping("/email-validation/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.verifyEmail(request.getEmail(), request.getCode());
    }

}
