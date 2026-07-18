package imwhs.eatz_server.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 현재 사용자의 로그인 관련 정보를 전달할 때 사용합니다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDto {

    private String email;

    private String role;

    private String token;

}
