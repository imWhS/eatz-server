package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus status;

    private final String errorCode;

    private final String message;

    public BaseException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BaseException(BaseErrorCode errorCode, String message) {
        super(message);
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.message = message;
    }

}
