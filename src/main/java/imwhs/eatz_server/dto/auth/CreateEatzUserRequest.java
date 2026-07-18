package imwhs.eatz_server.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 등록(사용자 생성)에 필요한 정보를 전달할 때 사용합니다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEatzUserRequest {

    @NotBlank(message = "사용자 이름은 필수 항목이에요.")
    private String username;

    @NotBlank(message = "이메일은 필수 항목이에요.")
    @Email(message = "이메일 주소 형식이 올바르지 않아요.")
    private String email;

    @NotBlank(message = "암호는 필수 항목이에요.")
    private String password;

}
