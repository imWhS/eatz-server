package imwhs.eatz_server.controller;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.auth.SignInRequestDto;
import imwhs.eatz_server.dto.auth.SignInResponseDto;
import imwhs.eatz_server.dto.auth.SignUpRequestDto;
import imwhs.eatz_server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Long>> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        Long userId = authService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<?>> signIn(@RequestBody @Valid SignInRequestDto dto) {
//        SignInResponseDto responseDto = authService.signIn(dto);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(responseDto));
//    }

}
