package imwhs.eatz_server.exception.base;

import imwhs.eatz_server.common.error.EatzErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    HttpStatus status;
    String code;
    String message;

    public BaseException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public BaseException(EatzErrorType errorType) {
        super(errorType.getMessage());
        this.status = errorType.getStatus();
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }

    public BaseException(EatzErrorType errorType, String customMessage) {
        super(customMessage);
        this.status = errorType.getStatus();
        this.code = errorType.getCode();
        this.message = customMessage;
    }

}
