package imwhs.eatz_server.dto.auth;

import lombok.Data;

@Data
public class EmailVerificationCodeRequest {

    private String email;

}
