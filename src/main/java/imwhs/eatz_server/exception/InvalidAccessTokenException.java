package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;
import lombok.Getter;

@Getter
public class InvalidAccessTokenException extends BaseAuthenticationException {

    public InvalidAccessTokenException() {
        super(ErrorCodeAuth.TOKEN_ACCESS_INVALID);
    }

}
