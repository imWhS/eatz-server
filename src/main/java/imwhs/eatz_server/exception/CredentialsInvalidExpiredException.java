package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import lombok.Getter;

@Getter
public class CredentialsInvalidExpiredException extends BaseAuthenticationException {

    public CredentialsInvalidExpiredException() {
        super(EatzAuthErrorType.CREDENTIALS_INVALID);
    }

}
