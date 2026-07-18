package imwhs.eatz_server.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest 클래스입니다.<br/>
 * 로그인에 필요한 정보를 전달할 때 사용합니다.
 */
@Data
public class LoginRequest {

    @Email
    @NotBlank
    @JsonProperty(required = true)
    private String email;

    @NotBlank
    @JsonProperty(required = true)
    private String password;

}
