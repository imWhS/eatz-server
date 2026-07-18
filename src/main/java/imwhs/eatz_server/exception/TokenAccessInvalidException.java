package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import lombok.Getter;

@Getter
public class TokenAccessInvalidException extends BaseAuthenticationException {

    public TokenAccessInvalidException() {
        super(EatzAuthErrorType.TOKEN_ACCESS_INVALID);
    }

}
