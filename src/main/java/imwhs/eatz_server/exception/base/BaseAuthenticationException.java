package imwhs.eatz_server.exception.base;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BaseAuthenticationException extends AuthenticationException {

    private final HttpStatus status;
    private final String code;
    private final String message;

    public BaseAuthenticationException(EatzAuthErrorType errorType) {
        super(errorType.getMessage());
        this.status = errorType.getStatus();
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }

    public BaseAuthenticationException(EatzAuthErrorType errorType, String customMessage) {
        super(customMessage);
        this.status = errorType.getStatus();
        this.code = errorType.getCode();
        this.message = customMessage;
    }

}