package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class AuthRateLimitExceededException extends BaseException {

    public AuthRateLimitExceededException() {
        super(EatzAuthErrorType.TOO_MANY_REQUESTS);
    }

}
