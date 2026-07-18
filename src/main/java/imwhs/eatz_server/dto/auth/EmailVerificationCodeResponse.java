package imwhs.eatz_server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationCodeResponse {

    private long dailyIssuableLimits;
    private long remainingIssuableAttempts;

}
