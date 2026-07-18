package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import lombok.Getter;

@Getter
public class RefreshTokenMissingException extends BaseAuthenticationException {

    public RefreshTokenMissingException() {
        super(EatzAuthErrorType.TOKEN_REFRESH_MISSING);
    }

}
