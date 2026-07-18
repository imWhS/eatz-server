package imwhs.eatz_server.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequest {

    @Email @NotBlank
    private String email;

    @NotBlank
    private String code;

}
