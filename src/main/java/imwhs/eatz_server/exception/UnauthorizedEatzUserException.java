package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;

public class UnauthorizedEatzUserException extends BaseAuthenticationException {

    public UnauthorizedEatzUserException() {
        super(EatzAuthErrorType.UNAUTHORIZED_USER);
    }

    public UnauthorizedEatzUserException(String message) {
        super(EatzAuthErrorType.UNAUTHORIZED_USER, message);
    }

}
