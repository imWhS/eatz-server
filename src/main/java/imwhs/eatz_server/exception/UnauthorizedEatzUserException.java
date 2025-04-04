package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;

public class UnauthorizedEatzUserException extends BaseException {

    public UnauthorizedEatzUserException() {
        super(ErrorCodeAuth.UNAUTHORIZED_USER);
    }

    public UnauthorizedEatzUserException(String message) {
        super(ErrorCodeAuth.UNAUTHORIZED_USER, message);
    }

}
