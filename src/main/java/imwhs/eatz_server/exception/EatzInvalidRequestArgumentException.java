package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class EatzInvalidRequestArgumentException extends BaseException {

    public EatzInvalidRequestArgumentException(String customMessage) {
        super(EatzCommonErrorType.INVALID_REQUEST_ARGUMENT, customMessage);
    }

    public EatzInvalidRequestArgumentException() {
        super(EatzCommonErrorType.INVALID_REQUEST_ARGUMENT);
    }

}
