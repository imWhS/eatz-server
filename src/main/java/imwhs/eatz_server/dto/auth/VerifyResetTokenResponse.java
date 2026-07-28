package imwhs.eatz_server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyResetTokenResponse {

    /**
     * 암호 설정 토큰
     */
    private String authorizedToken;

    /**
     * 계정의 마스킹 처리된 이메일 주소
     */
    private String maskedEmail;

}