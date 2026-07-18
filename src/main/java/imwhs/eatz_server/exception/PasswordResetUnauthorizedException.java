package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.common.error.EatzAuthPasswordResetErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import imwhs.eatz_server.exception.base.BaseException;
import lombok.Getter;

@Getter
public class PasswordResetUnauthorizedException extends BaseException {

    public PasswordResetUnauthorizedException() {
        super(EatzAuthPasswordResetErrorType.UNAUTHORIZED);
    }

}
