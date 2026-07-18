package imwhs.eatz_server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmPasswordResetRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, message = "암호는 8자리 이상의 길이여야 합니다.")
    private String newPassword;

}