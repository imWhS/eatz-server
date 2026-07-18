package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import lombok.Getter;

@Getter
public class TokenAccessExpiredException extends BaseAuthenticationException {

    public TokenAccessExpiredException() {
        super(EatzAuthErrorType.TOKEN_ACCESS_EXPIRED);
    }

}
