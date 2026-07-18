package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class InternalServerErrorException extends BaseException {

    public InternalServerErrorException() {
        super(EatzCommonErrorType.INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String customMessage) {
        super(EatzCommonErrorType.INTERNAL_SERVER_ERROR, customMessage);
    }

}
