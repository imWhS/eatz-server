package imwhs.eatz_server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyResetTokenResponse {

    /**
     * 암호 초기화 토큰
     */
    private String resetToken;

}