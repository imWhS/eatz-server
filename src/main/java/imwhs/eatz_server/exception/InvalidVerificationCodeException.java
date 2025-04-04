package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;
import lombok.Getter;

@Getter
public class InvalidVerificationCodeException extends BaseAuthenticationException {

    public InvalidVerificationCodeException() {
        super(ErrorCodeAuth.INVALID_VERIFICATION_CODE);
    }

}
