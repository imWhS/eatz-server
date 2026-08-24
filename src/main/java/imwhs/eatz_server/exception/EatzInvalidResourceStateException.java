package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class EatzInvalidResourceStateException extends BaseException {

    public EatzInvalidResourceStateException(String customMessage) {
        super(EatzCommonErrorType.INVALID_REQUEST_ARGUMENT, customMessage);
    }

    public EatzInvalidResourceStateException() {
        super(EatzCommonErrorType.INVALID_REQUEST_ARGUMENT);
    }

}
