package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BaseAuthenticationException extends AuthenticationException {

    private final HttpStatus status;

    private final String errorCode;

    private final String message;

    public BaseAuthenticationException(ErrorCodeAuth errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}