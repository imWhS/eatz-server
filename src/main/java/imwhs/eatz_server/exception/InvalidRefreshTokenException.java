package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;
import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends BaseAuthenticationException {

    public InvalidRefreshTokenException() {
        super(ErrorCodeAuth.TOKEN_REFRESH_INVALID);
    }

}
